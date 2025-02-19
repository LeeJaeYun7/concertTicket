package concert.domain.waitingqueue.services;

import concert.domain.waitingqueue.entities.WaitingDTO;
import concert.domain.waitingqueue.entities.dao.*;
import concert.domain.waitingqueue.entities.enums.RedisKey;
import concert.domain.waitingqueue.entities.vo.WaitingRankVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingQueueService {

  private final RedissonClient redissonClient;

  private final WaitingQueueDAO waitingQueueDAO;

  private final ActiveQueueDAO activeQueueDAO;

  private final WaitingQueueStatusPublisher waitingQueueStatusPublisher;

  private final WaitingQueueStatusDAO waitingQueueStatusDAO;

  private final TokenPublisher tokenPublisher;
  private static final String WAITING_QUEUE_STATUS_INACTIVE = "inactive";
  private static final long MAX_ACTIVE_QUEUE_SIZE = 5000L;
  private static final long MAX_TRANSFER_COUNT = 250L;

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
    // 분산 락을 사용하여 동시에 한 서버만 작업을 하도록 함
    RLock lock = redissonClient.getLock(RedisKey.ACTIVE_QUEUE_LOCK);
    lock.lock();

    try {
          long activeQueueSize = activeQueueDAO.getActiveQueueSize();
          long transferCount = Math.min(MAX_ACTIVE_QUEUE_SIZE - activeQueueSize, MAX_TRANSFER_COUNT);

          if(transferCount == 0){
             return;
          }

          Collection<WaitingDTO> tokenList = waitingQueueDAO.getAllWaitingTokens(transferCount);

          if (tokenList.isEmpty()) {
              return;
          }

          activeQueueDAO.migrateTokensFromWaitingQueueToActiveQueue(tokenList);

          // 토큰 목록을 Redis Pub/Sub을 통해 발행한다
          // 발행한 토큰 목록은 WebSocket 서버에서 Redis Pub/Sub을 통해 구독한다
          // 토큰 Pub/Sub의 목적은, 토큰이 활성화되었을 때, 웹소켓 클라이언트에게 알림을 주기 위함이다.
          tokenPublisher.publishAllActiveTokens(tokenList);

          // 토큰 목록을 대기열에서 삭제처리한다
          waitingQueueDAO.deleteWaitingQueueTokens(tokenList);

          Collection<WaitingDTO> waitingTokenList = waitingQueueDAO.getAllWaitingTokensWithRank();
          tokenPublisher.publishAllWaitingTokens(waitingTokenList);

        } finally {
          lock.unlock();  // 작업 완료 후 락을 해제
        }
  }


  public void removeTokenFromQueues(String token) {
    boolean waitingQueueExists = waitingQueueDAO.isTokenExistsInWaitingQueue(token);

    if(waitingQueueExists){
      waitingQueueDAO.deleteWaitingQueueToken(token);
      return;
    }

    WaitingDTO waitingDTO = WaitingDTO.parse(token);
    boolean activeQueueExists = activeQueueDAO.isTokenExistsInActiveQueue(waitingDTO);

    if(activeQueueExists){
      activeQueueDAO.deleteActiveQueueToken(waitingDTO.getUuid());
    }
  }

  public void clearAllQueues(){
     waitingQueueDAO.clearWaitingQueue();
     activeQueueDAO.clearActiveQueue();
  }

  public String getWaitingQueueStatus(){
    return waitingQueueStatusDAO.getWaitingQueueStatus();
  }

  public String getWaitingQueueStatusLastChanged(){
    return waitingQueueStatusDAO.getWaitingQueueStatusLastChanged();
  }

  public void changeWaitingQueueStatus(String status){
    long now = System.currentTimeMillis();

    waitingQueueStatusDAO.changeWaitingQueueStatus(status, now);
    waitingQueueStatusPublisher.publishWaitingQueueStatus(status);
    // 대기열이 비활성화되면, 대기열, 활성화열, 그리고 활성화토큰 정보를 모두 삭제처리하였습니다
    if(WAITING_QUEUE_STATUS_INACTIVE.equals(status)) {
       clearAllQueues();
    }
    log.info("[QUEUE] Waiting queue status has been changed!");
  }
}
