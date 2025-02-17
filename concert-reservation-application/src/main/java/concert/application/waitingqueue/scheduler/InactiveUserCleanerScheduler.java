package concert.application.waitingqueue.scheduler;

import concert.domain.waitingqueue.entities.dao.TokenHeartbeatDAO;
import concert.domain.waitingqueue.services.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InactiveUserCleanerScheduler {

    private final WaitingQueueService waitingQueueService;
    private final TokenHeartbeatDAO tokenHeartbeatDAO;
    private static final long INACTIVITY_THRESHOLD = 30 * 1000; // 30초 (밀리초 단위)

    @Scheduled(fixedRate = 10000)  // 10초마다 실행
    public void detectInactiveUsers() {
        RMap<String, String> heartbeatMap = tokenHeartbeatDAO.getTokenHeartbeat();

        // 현재 시간을 밀리초 단위로 구함
        long currentTime = System.currentTimeMillis();

        // Map을 순회하면서 이탈한 사용자 감지
        Iterator<String> iterator = heartbeatMap.keySet().iterator();

        List<String> tokensToRemove = new ArrayList<>();

        while (iterator.hasNext()) {
            String token = iterator.next();
            String timestampStr = heartbeatMap.get(token);

            if (timestampStr != null) {
                try {
                    // 타임스탬프를 밀리초로 변환
                    long timestamp = Long.parseLong(timestampStr);

                    // 만약 30초 이상 경과한 사용자라면 이탈한 사용자로 간주하고 삭제
                    if (currentTime - timestamp > INACTIVITY_THRESHOLD) {
                        // 이탈한 사용자 처리 (예: 대기열에서 제거)
                        tokensToRemove.add(token);
                    }
                } catch (NumberFormatException e) {
                    log.error("Wrong timestamp number format", e);
                }
            }
        }

        for (String token : tokensToRemove) {
            heartbeatMap.remove(token);
            waitingQueueService.removeUserTokenAndSession(token);
        }
    }
}


