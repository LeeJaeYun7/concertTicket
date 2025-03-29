package concert.domain.waitingqueue.services;

import concert.domain.waitingqueue.entities.WaitingDTO;
import concert.domain.waitingqueue.entities.dao.*;
import concert.domain.waitingqueue.entities.vo.WaitingRankVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueDAO waitingQueueDAO;
    private final ActiveQueueDAO activeQueueDAO;
    private final WaitingQueueStatusPublisher waitingQueueStatusPublisher;
    private final WaitingQueueStatusDAO waitingQueueStatusDAO;
    private final TokenPublisher tokenPublisher;
    private final RedissonClient redissonClient;

    private static final String WAITING_QUEUE_STATUS_INACTIVE = "inactive";
    private static final long MAX_ACTIVE_QUEUE_SIZE = 5000L;
    private static final long MAX_TRANSFER_COUNT = 250L;
    private static final String QUEUE_NAME = "waitingQueueRetryQueue"; // 큐 이름
    private static final int MAX_RETRY_COUNT = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 30000L; // 30초
    private static final long MAX_RETRY_DELAY_MS = 120000L; // 120초

    // 지수 백오프 방식으로 토큰을 발행
    private void publishWithExponentialBackoff(List<WaitingDTO> tokenList) {
        int retryCount = 0;
        boolean success = false;
        long retryDelay = INITIAL_RETRY_DELAY_MS;

        while (retryCount < MAX_RETRY_COUNT && !success) {
            try {
                tokenPublisher.publishAllActiveTokens(tokenList); // 토큰 발행 시도
                success = true; // 발행 성공
            } catch (Exception e) {
                retryCount++;
                log.error("Failed to publish tokens, attempt {}/{}: {}", retryCount, MAX_RETRY_COUNT, e.getMessage());

                if (retryCount < MAX_RETRY_COUNT) {
                    try {
                        long delay = Math.min(retryDelay, MAX_RETRY_DELAY_MS);
                        log.info("Retrying in {} ms", delay);
                        TimeUnit.MILLISECONDS.sleep(delay); // 지수 백오프 적용
                        retryDelay *= 2; // 지수 백오프
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.error("Max retry attempts reached, adding tokens to retry queue.");
                    enqueueFailedTokens(tokenList); // 재시도 실패 시 큐에 저장
                }
            }
        }
    }

    // 큐에 실패한 토큰을 저장하는 메서드
    private void enqueueFailedTokens(List<WaitingDTO> tokenList) {
        RQueue<WaitingDTO> queue = redissonClient.getQueue(QUEUE_NAME);
        queue.addAll(tokenList);
        log.info("Failed tokens added to retry queue: {}", tokenList);
    }

    // 큐에서 실패한 토큰을 재처리하는 메서드
    private void retryFailedTokens() {
        RQueue<WaitingDTO> queue = redissonClient.getQueue(QUEUE_NAME);
        while (!queue.isEmpty()) {
            WaitingDTO token = queue.poll();
            try {
                tokenPublisher.publishAllActiveTokens(List.of(token)); // 개별 토큰 처리
                log.info("Successfully retried token: {}", token);
            } catch (Exception e) {
                log.error("Retry failed for token {}: {}", token.getUuid(), e.getMessage());
                enqueueFailedTokens(List.of(token)); // 실패한 토큰 다시 큐에 저장
            }
        }
    }

    public String retrieveToken(String uuid) {
        WaitingDTO waitingDTO = WaitingDTO.of(uuid);
        return waitingQueueDAO.storeTokenIfWaitingQueueActive(waitingDTO);
    }

    public WaitingRankVO retrieveWaitingRank(String userToken) {
        Collection<WaitingDTO> tokenList = waitingQueueDAO.getAllWaitingTokens();
        long rank = 1L;

        // rank 계산
        for (WaitingDTO token : tokenList) {
            if (token.isUuidEquals(userToken)) {
                break;
            }
            rank += 1;
        }

        // uuid가 대기열에 없음 -> -1을 반환
        if (rank > tokenList.size()) {
            rank = -1;
        }

        if (rank == -1) {
            return WaitingRankVO.of(rank, "active");
        }

        return WaitingRankVO.of(rank, "waiting");
    }

    public void migrateFromWaitingToActiveQueue() {
        long activeQueueSize = activeQueueDAO.getActiveQueueSize();
        long transferCount = Math.min(MAX_ACTIVE_QUEUE_SIZE - activeQueueSize, MAX_TRANSFER_COUNT);

        if (transferCount == 0) {
            return;
        }

        Collection<WaitingDTO> tokenList = waitingQueueDAO.getAllWaitingTokens(transferCount);

        if (tokenList.isEmpty()) {
            return;
        }

        activeQueueDAO.migrateTokensFromWaitingQueueToActiveQueue(tokenList);

        // 지수 백오프 방식으로 토큰 발행
        publishWithExponentialBackoff(new ArrayList<>(tokenList));

        // 대기열에서 삭제
        waitingQueueDAO.deleteWaitingQueueTokens(tokenList);

        Collection<WaitingDTO> waitingTokenList = waitingQueueDAO.getAllWaitingTokensWithRank();
        tokenPublisher.publishAllWaitingTokens(waitingTokenList);  // 대기열 상태 발행
    }

    public void removeTokenFromQueues(String token) {
        boolean waitingQueueExists = waitingQueueDAO.isTokenExistsInWaitingQueue(token);

        if (waitingQueueExists) {
            waitingQueueDAO.deleteWaitingQueueToken(token);
            return;
        }

        WaitingDTO waitingDTO = WaitingDTO.parse(token);
        boolean activeQueueExists = activeQueueDAO.isTokenExistsInActiveQueue(waitingDTO);

        if (activeQueueExists) {
            activeQueueDAO.deleteActiveQueueToken(waitingDTO.getUuid());
        }
    }

    public void clearAllQueues() {
        waitingQueueDAO.clearWaitingQueue();
        activeQueueDAO.clearActiveQueue();
    }

    public String getWaitingQueueStatus() {
        return waitingQueueStatusDAO.getWaitingQueueStatus();
    }

    public String getWaitingQueueStatusLastChanged() {
        return waitingQueueStatusDAO.getWaitingQueueStatusLastChanged();
    }

    public void changeWaitingQueueStatus(String status) {
        long now = System.currentTimeMillis();

        waitingQueueStatusDAO.changeWaitingQueueStatus(status, now);
        waitingQueueStatusPublisher.publishWaitingQueueStatus(status);

        // 대기열이 비활성화되면, 대기열, 활성화열, 그리고 활성화토큰 정보를 모두 삭제처리
        if (WAITING_QUEUE_STATUS_INACTIVE.equals(status)) {
            clearAllQueues();
        }
        log.info("[QUEUE] Waiting queue status has been changed!");
    }

    // 큐에서 실패한 토큰을 주기적으로 재처리할 수 있는 메서드
    public void processRetryQueue() {
        retryFailedTokens();
    }
}
