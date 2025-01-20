package concert.domain.waitingQueue.domain;

public interface WaitingQueueDao {

    String addToWaitingQueue(long concertId, String uuid);

    long getWaitingRank(long concertId, String uuid);

    String getActiveQueueToken(long concertId, String uuid);

    void deleteActiveQueueToken(long concertId, String uuid);

    void removeTop333FromWaitingQueue(long concertId);

}
