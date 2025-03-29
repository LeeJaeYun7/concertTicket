package concert.interfaces.waitingqueue;

import concert.application.waitingqueue.business.WaitingQueueApplicationService;
import concert.application.waitingqueue.business.WaitingQueueMigrationApplicationService;
import concert.domain.waitingqueue.entities.vo.TokenVO;
import concert.domain.waitingqueue.entities.vo.WaitingRankVO;
import concert.interfaces.waitingqueue.response.TokenResponse;
import concert.interfaces.waitingqueue.response.WaitingRankResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueController {

  private final WaitingQueueMigrationApplicationService waitingQueueMigrationApplicationService;
  private final WaitingQueueApplicationService waitingQueueApplicationService;
  private static final long activationTriggerTraffic = 1500L;
  private static final long deactivationTriggerTraffic = 300L;

  @PostMapping("/api/v1/waitingQueue/migration")
  public ResponseEntity<Boolean> processWaitingQueueMigration() {
      try {
          waitingQueueMigrationApplicationService.migrateFromWaitingToActiveQueue();
          return ResponseEntity.ok(true); // 성공 시 true 반환
      } catch (Exception e) {
          log.error("대기열 처리 중 오류 발생: " + e.getMessage(), e);
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false); // 실패 시 false 반환
      }
  }

  @PostMapping("/api/v1/waitingQueue/activate")
  public ResponseEntity<Void> activateQueue() {
      waitingQueueApplicationService.activateWaitingQueue(activationTriggerTraffic);
      return ResponseEntity.ok().build();
  }

  @PostMapping("/api/v1/waitingQueue/deactivate")
  public ResponseEntity<Void> deactivateQueue() {
      waitingQueueApplicationService.deactivateWaitingQueue(deactivationTriggerTraffic);
      return ResponseEntity.ok().build();
  }

  @GetMapping("/api/v1/waitingQueue/token")
  public ResponseEntity<TokenResponse> retrieveToken(@RequestParam(value = "uuid") String uuid) {
      TokenVO tokenVO = waitingQueueApplicationService.retrieveToken(uuid);
      TokenResponse tokenResponse = TokenResponse.of(tokenVO.getToken());

      return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
  }

  @GetMapping("/api/v1/waitingQueue/rank")
  public ResponseEntity<WaitingRankResponse> retrieveWaitingRank(@RequestParam(value = "token") String token) {
      WaitingRankVO waitingRankVo = waitingQueueApplicationService.retrieveWaitingRank(token);
      WaitingRankResponse waitingRankResponse = WaitingRankResponse.of(waitingRankVo.getWaitingRank(), waitingRankVo.getStatus());

      return ResponseEntity.status(HttpStatus.CREATED).body(waitingRankResponse);
  }
}
