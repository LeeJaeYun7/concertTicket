package concert.application.waitingqueue.scheduler;

import concert.application.waitingqueue.business.WaitingQueueMigrationApplicationService;
import concert.domain.concert.services.ConcertService;
import concert.domain.shared.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WaitingQueueScheduler {

  private final ConcertService concertService;

  private final WaitingQueueMigrationApplicationService waitingQueueMigrationApplicationService;

  @Scheduled(fixedRate = 10000)
  public void processWaitingQueue() {
      try {
        waitingQueueMigrationApplicationService.migrateFromWaitingToActiveQueue();
      } catch (CustomException e) {
        log.error("대기열을 처리하는 중 에러 발생: " + e.getMessage());
      }
  }
}
