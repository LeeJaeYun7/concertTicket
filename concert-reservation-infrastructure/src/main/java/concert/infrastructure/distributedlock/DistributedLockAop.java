package concert.infrastructure.distributedlock;

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

    @Around("@annotation(concert.infrastructure.distributedlock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        System.out.println("key는?" + key);

        RLock rLock = redissonClient.getLock(key);

        log.info("WaitTime: {}", distributedLock.waitTime());
        log.info("LeaseTime: {}", distributedLock.leaseTime());

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());  // (2)

            if (!available) {
                log.warn("Unable to acquire lock for service: {}, key: {}", method.getName(), key);
                return false;
            }

            log.info("Successfully acquired lock for service: {}, key: {}", method.getName(), key);
            return aopForTransaction.proceed(joinPoint);  // (3)
        } catch (InterruptedException e) {
            throw new InterruptedException();
        }
//        } finally {
//            try {
//                rLock.unlock();   // (4)
//            } catch (IllegalMonitorStateException e) {
//                log.info("Redisson Lock already unlocked for service: {}, key: {}",
//                        method.getName(),
//                        key
//                );
//            }
//        }
    }
}