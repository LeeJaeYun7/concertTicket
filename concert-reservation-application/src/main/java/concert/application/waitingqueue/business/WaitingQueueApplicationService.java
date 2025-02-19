package concert.application.waitingqueue.business;

import concert.application.waitingqueue.business.enums.WaitingQueueStatus;
import concert.application.waitingqueue.business.enums.WaitingQueueStatusTrigger;
import concert.domain.member.services.MemberService;
import concert.domain.waitingqueue.entities.vo.TokenVO;
import concert.domain.waitingqueue.services.WaitingQueueService;
import concert.domain.waitingqueue.entities.vo.WaitingRankVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 대기열의 활성화,비활성화를 주로 다루는 서비스 클래스
@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingQueueApplicationService {

  private final MemberService memberService;

  private final WaitingQueueService waitingQueueService;

  public void activateWaitingQueue(long totalTraffic) {
    String currentStatus = waitingQueueService.getWaitingQueueStatus();
    String lastChangedStr = waitingQueueService.getWaitingQueueStatusLastChanged();
    long lastChanged = lastChangedStr != null ? Long.parseLong(lastChangedStr) : 0;

    if (WaitingQueueStatus.ACTIVE.equals(currentStatus)) {
      log.info("[QUEUE] Waiting queue is already active.");
      return;
    }

    // 대기열 활성화 조건으로
    // 1. 이전 대기열 상태 변경 시점 기준 3분이 지나야만 가능하거나
    // 2. 트래픽이 1500이상이 되면 즉시 가능하도록 하였습니다.
    // 1번의 조건은 트래픽 변동폭이 큰 시점에서 대기열 On/Off가 너무 자주 발생하지 않도록 3분의 Delay time을 적용하였습니다
    // 2번의 조건은, 그럼에도 불구하고 Peak 트래픽이 발생하는 경우, 이에 대응하여 대기열 발동이 필요하다고 판단하여, Peak 트래픽의 기준을 1500으로 잡고, 대기열 활성화를 추가하였습니다.
    if(isWaitingQueueStatusChangeRequired(WaitingQueueStatus.INACTIVE, lastChanged, totalTraffic)){
      waitingQueueService.changeWaitingQueueStatus(WaitingQueueStatus.ACTIVE);
    }
  }

  public void deactivateWaitingQueue(long totalTraffic) {
    String currentStatus = waitingQueueService.getWaitingQueueStatus();
    String lastChangedStr = waitingQueueService.getWaitingQueueStatusLastChanged();
    long lastChanged = lastChangedStr != null ? Long.parseLong(lastChangedStr) : 0;

    if (WaitingQueueStatus.INACTIVE.equals(currentStatus)) {
      log.info("[QUEUE] Waiting queue is already inactive.");
      return;
    }

    // 대기열 비활성화 조건으로
    // 1. 마찬가지로 이전 대기열 상태 변경 시점 기준 3분이 지나야만 가능하거나
    // 2. 트래픽이 300이하가 되면 즉시 비활성화하도록 하였습니다.
    // 1번의 조건은 트래픽 변동폭이 큰 시점에서 대기열 On/Off가 너무 자주 발생하지 않도록 3분의 Delay time을 적용하였습니다
    // 2번의 조건은, 트래픽이 매우 낮은 경우, 대기열이 필요하지 않으므로, 즉시 대기열 비활성화를 하도록 하였습니다.
    if(isWaitingQueueStatusChangeRequired(WaitingQueueStatus.ACTIVE, lastChanged, totalTraffic)) {
      waitingQueueService.changeWaitingQueueStatus(WaitingQueueStatus.INACTIVE);
    }
  }

  public boolean isWaitingQueueStatusChangeRequired(String currentStatus, long lastChanged, long totalTraffic){
    if(hasExceededCoolDownTime(lastChanged)){
       return true;
    }

    if(shouldActivateQueue(currentStatus, totalTraffic) || shouldDeactivateQueue(currentStatus, totalTraffic)){
       return true;
    }

    return false;
  }

  public boolean hasExceededCoolDownTime(long lastChanged){
    long now = System.currentTimeMillis();
    return (now - lastChanged) > WaitingQueueStatusTrigger.COOLDOWN_TIME;
  }

  public boolean shouldActivateQueue(String currentStatus, long totalTraffic){
      return WaitingQueueStatus.INACTIVE.equals(currentStatus) && (totalTraffic >= WaitingQueueStatusTrigger.ACTIVATION_TRIGGER_TRAFFIC);
  }

  public boolean shouldDeactivateQueue(String currentStatus, long totalTraffic){
      return WaitingQueueStatus.ACTIVE.equals(currentStatus) && (totalTraffic <= WaitingQueueStatusTrigger.DEACTIVATION_TRIGGER_TRAFFIC);
  }

  public TokenVO retrieveToken(String uuid) {
    memberService.getMemberByUuid(uuid);
    String token = waitingQueueService.retrieveToken(uuid);
    return TokenVO.of(token);
  }

  public WaitingRankVO retrieveWaitingRank(String token) {
    return waitingQueueService.retrieveWaitingRank(token);
  }
}
