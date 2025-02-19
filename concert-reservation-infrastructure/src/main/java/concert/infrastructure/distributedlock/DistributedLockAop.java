package concert.infrastructure.distributedlock;

import concert.infrastructure.distributedlock.dao.TokenConcertScheduleSeatDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;
    private final TokenConcertScheduleSeatDAO tokenConcertScheduleSeatDAO;


    @Around("@annotation(concert.infrastructure.distributedlock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        String concertScheduleSeatId = key.replace("LOCK:CONCERT_SCHEDULE_SEAT_RESERVATION:", "");

        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());  // (2)

            if (!available) {
                log.warn("Unable to acquire lock for service: {}, key: {}", method.getName(), key);
                return false;
            }

            log.info("Successfully acquired lock for service: {}, key: {}", method.getName(), key);

            String token = extractToken(signature.getParameterNames(), joinPoint.getArgs());

            if (token != null) {
                tokenConcertScheduleSeatDAO.saveTokenScheduleSeat(concertScheduleSeatId, token);
            }

            return aopForTransaction.proceed(joinPoint);  // (3)
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally{
            rLock.unlock();
        }
    }

    public void unlockConcertScheduleSeat(String concertScheduleSeatId) {
        String key = REDISSON_LOCK_PREFIX + "CONCERT_SCHEDULE_SEAT_RESERVATION:" + concertScheduleSeatId;
        RLock rLock = redissonClient.getLock(key);

        if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
            rLock.unlock();
            log.info("Unlocked seat reservation for concertScheduleSeatId: {}", concertScheduleSeatId);
        } else {
            log.warn("No active lock found for concertScheduleSeatId: {}", concertScheduleSeatId);
        }
    }

    private String extractToken(String[] paramNames, Object[] args) {
        for (int i = 0; i < paramNames.length; i++) {
            if ("token".equals(paramNames[i]) && args[i] instanceof String) {
                return (String) args[i];
            }
        }
        return null;
    }
}