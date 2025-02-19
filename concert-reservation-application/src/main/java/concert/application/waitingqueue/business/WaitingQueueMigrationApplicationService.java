package concert.application.waitingqueue.business;

import concert.application.waitingqueue.business.enums.WaitingQueueStatus;
import concert.domain.waitingqueue.services.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WaitingQueueMigrationApplicationService {

  private final WaitingQueueService waitingQueueService;

  public void migrateFromWaitingToActiveQueue() {
    String currentStatus = waitingQueueService.getWaitingQueueStatus();
    if(WaitingQueueStatus.ACTIVE.equals(currentStatus)) {
      waitingQueueService.migrateFromWaitingToActiveQueue();
    }
  }
}
