package concert.interfaces.waitingqueue;

import concert.application.waitingqueue.business.WaitingQueueApplicationService;
import concert.domain.waitingqueue.entities.vo.TokenVO;
import concert.domain.waitingqueue.entities.vo.WaitingRankVO;
import concert.interfaces.waitingqueue.response.TokenResponse;
import concert.interfaces.waitingqueue.response.WaitingRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingQueueController {

  private final WaitingQueueApplicationService waitingQueueApplicationService;
  private static final long activationTriggerTraffic = 1500L;
  private static final long deactivationTriggerTraffic = 300L;

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
