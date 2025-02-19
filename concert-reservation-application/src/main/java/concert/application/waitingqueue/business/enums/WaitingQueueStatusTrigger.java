package concert.application.waitingqueue.business.enums;

public interface WaitingQueueStatusTrigger {
    long ACTIVATION_TRIGGER_TRAFFIC = 1500L;
    long DEACTIVATION_TRIGGER_TRAFFIC = 300L;
    long COOLDOWN_TIME = 180_000L;
}
