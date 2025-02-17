package concert.domain.waitingqueue.entities.dao;

import concert.domain.waitingqueue.entities.WaitingDTO;
import concert.domain.waitingqueue.entities.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingQueueDAO {

  private final RedissonClient redisson;

  public String addToWaitingQueue(WaitingDTO waitingDTO) {
    RSortedSet<String> waitingQueue = redisson.getSortedSet(RedisKey.WAITING_QUEUE);
    String token = waitingDTO.getToken();
    waitingQueue.add(token);
    return token;
  }

  public Collection<WaitingDTO> getAllWaitingTokens() {
    RSortedSet<String> waitingQueue = redisson.getSortedSet(RedisKey.WAITING_QUEUE);
    Collection<String> tokenList = waitingQueue.readAll();

    return tokenList.stream().map(WaitingDTO::parse).collect(Collectors.toList());
  }

  public Collection<WaitingDTO> getAllWaitingTokens(long transferCount) {
    RSortedSet<String> waitingQueue = redisson.getSortedSet(RedisKey.WAITING_QUEUE);
    Collection<String> tokenList = waitingQueue.readAll();

    Collection<String> limitedList = tokenList.stream()
                                              .limit(transferCount)
                                              .toList();

    return limitedList.stream().map(WaitingDTO::parse).collect(Collectors.toList());
  }


  public boolean isTokenExistsInWaitingQueue(String token){
      RSortedSet<String> waitingQueue = redisson.getSortedSet(RedisKey.WAITING_QUEUE);
      return waitingQueue.contains(token);
  }

  public void deleteWaitingQueueToken(String token){
      RSortedSet<String> waitingQueue = redisson.getSortedSet(RedisKey.WAITING_QUEUE);
      waitingQueue.remove(token);
  }

  public void deleteWaitingQueueTokens(Collection<WaitingDTO> tokens) {
    RSortedSet<String> waitingQueue = redisson.getSortedSet(RedisKey.WAITING_QUEUE);

    tokens.forEach(waitingDTO -> {
      String token = waitingDTO.getToken();
      waitingQueue.remove(token);
    });
  }


  public String storeTokenIfWaitingQueueActive(WaitingDTO waitingDTO) {
    RBucket<String> waitingQueueStatusBucket = redisson.getBucket(RedisKey.WAITING_QUEUE_STATUS);

    if (!RedisKey.WAITING_QUEUE_STATUS_ACTIVE.equals(waitingQueueStatusBucket.get())) {
      return waitingDTO.getToken();
    }

    return addToWaitingQueue(waitingDTO);
  }


  public void clearWaitingQueue() {
    // 대기열 비우기 (RSortedSet)
    RSortedSet<String> waitingQueue = redisson.getSortedSet(RedisKey.WAITING_QUEUE);
    waitingQueue.clear();  // 대기열의 모든 데이터 삭제
  }
}

