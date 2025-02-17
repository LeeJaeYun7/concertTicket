package concert.domain.waitingqueue.entities.dao;

import concert.domain.waitingqueue.entities.WaitingDTO;
import concert.domain.waitingqueue.entities.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenPublisher {

    private final RedissonClient redissonClient;

    public void publishAllActiveTokens(Collection<WaitingDTO> tokenList){
        List<String> tokens = tokenList.stream().map(WaitingDTO::getToken).collect(Collectors.toList());

        RTopic topic = redissonClient.getTopic(RedisKey.TOKEN_PUB_SUB_CHANNEL);
        topic.publish(tokens);  // tokenList를 발행
    }
}
