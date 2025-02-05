package concert.application.shared.scheduler;

import concert.application.waitingqueue.business.WaitingQueueApplicationService;
import concert.infrastructure.monitoring.TrafficMonitoringFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrafficScheduler {

    private final WaitingQueueApplicationService waitingQueueApplicationService;
    private final TrafficMonitoringFilter trafficMonitoringFilter;

    // 모니터링할 API 목록
    private final List<String> apiList = List.of(
            "/api/v1/concertScheduleSeat/active",
            "/api/v1/concertScheduleSeat/reservation",
            "/api/v1/concertSchedule",
            "/api/v1/order"
    );

    // 매 1초마다 실행 (1,000ms)
    @Scheduled(fixedRate = 1000)
    public void aggregateTraffic() {

        long totalTraffic = 0;

        for (String api : apiList) {
            long traffic = trafficMonitoringFilter.getTrafficForLastMinute(api);
            totalTraffic += traffic;
            log.info("[TRAFFIC] API: {}, Calls in Last 1 Min: {}", api, traffic);
        }

        if (totalTraffic > 1000) {
            log.info("[QUEUE] Traffic exceeded 1000! Activating waiting queue...");
            waitingQueueApplicationService.activateWaitingQueue(); // 대기열 활성화
        } else if (totalTraffic < 800) {
            log.info("[QUEUE] Traffic dropped below 800! Deactivating waiting queue...");
            waitingQueueApplicationService.deactivateWaitingQueue();
        }
    }
}
