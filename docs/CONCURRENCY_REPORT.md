

# 동시성 제어 보고서 

## 1. 콘서트 대기열 시스템에서 동시성 문제가 발생할 수 있는 로직 

### 1) 잔액 충전

**(1) 발생 원인**<br>
-  한 명의 사용자가 잔액을 충전할 때, 같은 요청을 여러 번 호출 할 수 있음<br> 
-> 이러한 경우, 1번의 요청만 승인되도록 해야 합니다. <br>
-> 이를 **멱등성(idempotent) 처리**라고 합니다. 

**(2) 목표**<br>
- 한 명의 사용자가 여러 번 충전 요청을 보내더라도, 잔액은 한 번만 증가해야 한다
- 충전 금액은 사용자 계정에 정확하게 반영되어야 한다.

<br> 

### 2) 좌석 예약 요청

**(1) 발생 원인**<br>
- **동시에 여러 명의 사용자가 하나의 좌석에 대해 예약 요청**을 할 수 있음  

**(2) 목표**<br>
- **특정 좌석에 대해 한 명의 사용자만 예약 요청이 성공**해야 한다
- 동시에 요청한 나머지 사용자들은 예약 요청이 실패해야 한다   

<br> 

### 3) 결제 요청(=예약 요청) 

**(1) 발생 원인**<br>
-  한 명의 사용자가 결제를 할 때, 같은 요청을 여러 번 호출할 수 있음<br>
-> 이러한 경우, 1번의 요청만 승인되도록 해야 합니다. <br>
-> 이를 **멱등성(idempotent) 처리**라고 합니다. 

**(2) 목표**<br>
- 한 명의 사용자가 여러 번 결제 요청을 보내더라도, 결제는 한 번만 되어야 한다.
- 결제 금액의 차감은 사용자 계정에 정확하게 반영되어야 한다.

<br> 

## 2. 동시성 제어 코드


### 1) 잔액 충전

**(1) 비관적 락(Pessimistic Lock) <br>**
- 잔액 충전 시, 우선적으로 **DB에서 멤버의 잔액 조회가 필요한데 DB 조회시 비관적 락**을 걸어주었습니다. <br> 
-> 비관적 락은 멤버 정보가 업데이트 되고, **JPA의 Dirty-Checking에 의해 DB에 커밋될 때 해제**됩니다.   

```
public ChargeResponse chargeBalance(UUID uuid, long amount) throws Exception {
        validateMember(uuid);

        Member member = memberService.getMemberByUuidWithLock(uuid);
        long balance = member.getBalance();
        long updatedBalance = balance + amount;
        member.updateBalance(updatedBalance);

        chargeService.createCharge(uuid, amount);

        return ChargeResponse.of(updatedBalance);
}
```
```
public Member getMemberByUuidWithLock(UUID uuid) throws Exception {
        return memberRepository.findByUuidWithLock(uuid)
                               .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND, Loggable.NEVER));
}
```
```
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT m from Member m WHERE m.uuid = :uuid")
Optional<Member> findByUuidWithLock(@Param("uuid") UUID uuid);
```

<br> 

### 2) 좌석 예약 요청 
**(1) 비관적 락(Pessimistic Lock) <br>**
- 좌석 선점 예약 시, 우선적으로 **DB에서 해당 좌석이 선점되었는지 조회가 필요한데 DB 조회시 비관적 락**을 걸어주었습니다. <br> 
-> 비관적 락은 좌석 선점 예약이 업데이트 될 때, **JPA의 Dirty-Checking에 의해 DB에 커밋되면서 해제**됩니다.   
```
public Seat getSeatByConcertScheduleIdAndNumberWithPessimisticLock(long concertScheduleId, long number) throws Exception {
        return seatRepository.findByConcertScheduleIdAndNumberWithPessimisticLock(concertScheduleId, number)
                             .orElseThrow(Exception::new);
}
```
```
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.concertSchedule.id = :concertScheduleId AND s.number = :number")
Optional<Seat> findByConcertScheduleIdAndNumberWithPessimisticLock(@Param("concertScheduleId") long concertScheduleId, @Param("number") long number);
```

<br> 

**(2) 낙관적 락(Optimistic Lock) <br>**
- 좌석 선점 예약 시, 우선적으로 **DB에서 해당 좌석이 선점되었는지 조회가 필요한데 DB 조회시 낙관적 락**을 걸어주었습니다. <br> 
-> **낙관적 락은 좌석 Entity에 Version 필드를 추가해서 관리**됩니다. <br> 
-> **여러 스레드가 경합하는 상황에서**, 한 스레드에 의해 버전 정보가 변동되었다면, **다른 스레드는 정보 업데이트가 불가능**합니다. <br> 
```

@Getter
@Entity
@Table(name = "seat")
@NoArgsConstructor
public class Seat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;
    private long number;
    private long price;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    ...
}
```

```
public Seat getSeatByConcertScheduleIdAndNumberWithOptimisticLock(long concertScheduleId, long number) {
        return seatRepository.findByConcertScheduleIdAndNumberWithOptimisticLock(concertScheduleId, number)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND, Loggable.ALWAYS));
}
```

```
@Lock(LockModeType.OPTIMISTIC)
@Query("SELECT s FROM Seat s WHERE s.concertSchedule.id = :concertScheduleId AND s.number = :number")
Optional<Seat> findByConcertScheduleIdAndNumberWithOptimisticLock(@Param("concertScheduleId") long concertScheduleId, @Param("number") long number);
```

<br>

**(3) 레디스 분산 락(Redis Distributed Lock) <br>**
- 좌석 선점 예약 시, **concertScheduleId와 좌석 번호를 결합한 정보를 Key로 Redis 분산 락을 생성**하였습니다. <br> 
-> 즉, **한 스레드가 Redis 분산 락을 획득 하면**, 다른 스레드는 그 락이 해제될 때까지 **해당 자원에 접근하지 못하게 됩니다**. <br>
-> Redis 분산 락 구현 시, **코드의 유지보수성을 고려해 AOP로 구현**하였습니다. <br>      
```
@DistributedLock(key = "#concertScheduleId + '_' + #number", waitTime = 500, leaseTime = 300000, timeUnit = TimeUnit.MILLISECONDS)
public Seat getSeatByConcertScheduleIdAndNumberWithDistributedLock(String lockName, long concertScheduleId, long number) {
        return seatRepository.findByConcertScheduleIdAndNumberWithDistributedLock(concertScheduleId, number)
                             .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND, Loggable.ALWAYS));
}
```

```
@Query("SELECT s FROM Seat s WHERE s.concertSchedule.id = :concertScheduleId AND s.number = :number")
Optional<Seat> findByConcertScheduleIdAndNumberWithDistributedLock(@Param("concertScheduleId") long concertScheduleId, @Param("number") long number);
```

<br> 


### 3) 결제 요청 
**(1) 비관적 락(Pessimistic Lock) <br>**

```
@Transactional
public ReservationResponse createReservation(String token, UUID uuid, long concertScheduleId, long seatNumber) throws Exception {
        validateToken(token);
        validateSeatReservation(concertScheduleId, seatNumber);
        checkBalanceOverPrice(uuid, concertScheduleId);

        ConcertSchedule concertSchedule = getConcertSchedule(concertScheduleId);
        Seat seat = seatService.getSeatByConcertScheduleIdAndNumberWithLock(concertScheduleId, seatNumber);
        long price = getConcertSchedule(concertScheduleId).getPrice();

        reservationService.createReservation(concertSchedule, uuid, seat, price);
        paymentService.createPayment(concertSchedule, uuid, price);
        memberService.decreaseBalance(uuid, price);

        updateStatus(token, concertScheduleId, seatNumber);

        String name = getMember(uuid).getName();
        String concertName = getConcert(concertScheduleId).getName();
        LocalDateTime dateTime = getConcertSchedule(concertScheduleId).getDateTime();

        return ReservationResponse.of(name, concertName, dateTime, price);
}
```
```
public void decreaseBalance(UUID uuid, long price) throws Exception {
        Member member = getMemberByUuidWithLock(uuid);
        member.updateBalance(member.getBalance()-price);
}
```
```
 public Member getMemberByUuidWithLock(UUID uuid) throws Exception {
        return memberRepository.findByUuidWithLock(uuid).orElseThrow(Exception::new);
}
```
```
@Lock(LockModeType.PESSIMISTIC_READ)
@Query("SELECT m from Member m WHERE m.uuid = :uuid")
Optional<Member> findByUuidWithLock(@Param("uuid") UUID uuid);
```

<br>

## 3. 동시성 테스트


### 1) 잔액 충전
**(1) 비관적 락(Pessimistic Lock) 동시성 테스트 <br>** 
```
@Test
@DisplayName("총 50번의 충전 요청 중 1번만 멤버 잔액에 반영된다")
void 총_50번의_충전_요청_중_1번만_멤버_잔액에_반영된다() throws InterruptedException {
            int requestCount = 50;
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(requestCount);

            for (int i = 0; i < requestCount; i++) {
                executorService.submit(() -> {
                    try {
                        chargeFacade.chargeBalance(memberUuid, 10000);
                        successCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            Member updatedMember = memberRepository.findByUuid(memberUuid).orElseThrow();

            assertEquals(1, successCount.get());
            assertEquals(10000, updatedMember.getBalance());
        }
```
![image](https://github.com/user-attachments/assets/f266cc2c-485d-4d8e-9eda-84799249bc49)

<br>

### 2) 좌석 예약 요청 
**(1) 비관적 락(Pessimistic Lock) 동시성 테스트 <br>**

```
@Test
@DisplayName("비관적 락을 활용해 1000번의 좌석 예약 요청 중 1번만 성공한다")
void 비관적_락을_활용해_1000번의_좌석_예약_요청_중_1번만_성공한다() throws InterruptedException {

            int requestCount = 1000;
            ExecutorService executorService = Executors.newFixedThreadPool(50);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(requestCount);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < requestCount; i++) {
                int finalI = i;

                executorService.submit(() -> {
                    try {
                        seatFacade.createSeatReservationWithPessimisticLock(savedMembers.get(finalI).getUuid(), savedConcertSchedule.getId(), savedSeat.getNumber());
                        successCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("Total time taken for 1000 requests: " + duration + " ms");

            assertEquals(1, successCount.get());
}

```
![image](https://github.com/user-attachments/assets/d27c0a06-bc8a-44e2-895b-0c15e78b04be)


<br> 

**(2) 낙관적 락(Optimistic Lock) 동시성 테스트 <br>**

```
@Test
@DisplayName("낙관적 락을 활용해 1000번의 좌석 예약 요청 중 1번만 성공한다")
void 낙관적_락을_활용해_1000번의_좌석_예약_요청_중_1번만_성공한다() throws InterruptedException {

            int requestCount = 1000;
            ExecutorService executorService = Executors.newFixedThreadPool(50);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(requestCount);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < requestCount; i++) {
                int finalI = i;

                executorService.submit(() -> {
                    try {
                        seatFacade.createSeatReservationWithOptimisticLock(savedMembers.get(finalI).getUuid(), savedConcertSchedule.getId(), savedSeat.getNumber());
                        successCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("Total time taken for 1000 requests: " + duration + " ms");

            assertEquals(1, successCount.get());
}
```
![image](https://github.com/user-attachments/assets/84d6042a-2787-4732-9214-d65e3fa4df0f)


<br> 

**(3) 레디스 분산 락(Redis Distributed Lock) 동시성 테스트 <br>**

```
@Test
@DisplayName("분산 락을 활용해 1000번의 좌석 예약 요청 중 1번만 성공한다")
void 분산_락을_활용해_1000번의_좌석_예약_요청_중_1번만_성공한다() throws InterruptedException {

            int requestCount = 1000;
            ExecutorService executorService = Executors.newFixedThreadPool(50);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(requestCount);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < requestCount; i++) {
                int finalI = i;
                
                executorService.submit(() -> {
                    try {
                        seatFacade.createSeatReservationWithDistributedLock(savedMembers.get(finalI).getUuid(), savedConcertSchedule.getId(), savedSeat.getNumber());
                        successCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("Total time taken for 1000 requests: " + duration + " ms");

            assertEquals(1, successCount.get());
        }
```
![image](https://github.com/user-attachments/assets/39c35e14-d257-4c5e-a09c-df7079f582ce)


<br> 

### 3) 결제 요청 
**(1) 비관적 락(Pessimistic Lock) 동시성 테스트 <br>**
```
@Test
@DisplayName("총 50번의 예약 요청 중 1번만 성공한다")
public void 총_50번의_예약_요청_중_1번만_성공한다() throws InterruptedException {
            int requestCount = 50;
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(requestCount);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < requestCount; i++) {
                executorService.submit(() -> {
                    try {
                        reservationFacade.createReservation(token, memberUuid, savedConcertSchedule.getId(), savedSeat.getNumber());
                        successCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("Total time taken for 50 requests: " + duration + " ms");

            assertEquals(1, successCount.get());
        }
```
![image](https://github.com/user-attachments/assets/e6f0a9d8-1f12-484c-adb7-7e330ea6d937)


## 3. 결론

### 1. 구현의 복잡도
- 비관적 락 == 낙관적 락 <<< Redis 분산락 <br> 
- 비관적 락과 낙관적 락은 JPA에서 제공하는 어노테이션으로 비교적 쉽게 구현이 가능하다 <br>
- 반면, Redis 분산락은 Redisson 라이브러리를 사용하는 경우, waitTime(대기 시간), leaseTime(락 유지 시간) 등을 지정해줘야 한다 <br>
-> 따라서, 내 비즈니스 요구사항에 맞춰서 해당 정책에 대한 기준을 수립해야 한다 <br>
-> 또한, Redis 분산락 같은 경우 AOP를 사용하여 구현하였기 때문에, 구현의 복잡도가 다소 높아졌다 <br>

### 2. 성능
- 좌석 예약 테스트(1000명) 기준으로 설명하면 <br> 
-> 낙관적 락 >= 비관적 락 >>> Redis 분산락 <br>
- 낙관적 락은 비관적 락과는 다르게 락이 해제되기를 기다리지 않아도 된다는 점 때문인지, 약간 성능이 우수하게 나왔다 <br>
- Redis 분산락 같은 경우는, waitTime 60ms로 설정했을 때, 낙관적 락, 비관적 락의 2배가 넘는 시간이 소요되었다. <br>
-> 그리고 waitTime을 늘릴수록 테스트 수행 시간도 더 길어졌다 <br>
-> 어느 정도의 waitTime, leaseTime을 설정하는 것이 적절한지에 대한 고민을 갖게 되었다 <br>


## 4. 앞으로 고민할 포인트
#### 1) Redis 분산락
(1) 비관적 락, 낙관적 락과 비교할 때 Redis 분산락이 어떤 이점이 있는가? <br>
-> 테스트 결과만을 봤을 때는, Redis 분산락이 갖는 경쟁우위가 없어 보인다. <br>

(2) Redis 분산락을 실무에서 적용할 때, waitTime, leaseTime 등에 대해서 어떻게 고민해야 하는가? <br>

(3) Redis 분산락이 Redis에서 잘 생성되었다가, 생성되지 않았다가 하는 문제가 반복되었다. <br>
-> 왜 이런 문제가 발생했는가? <br>
  
(4) Redis 분산락을 생성하면 key 형태로 Redis에 생성이 된다 <br> 
-> 실제로는 Redis 분산락 이외에 Redis 캐시 등 다양한 Key들이 존재할텐데, Key들은 어떤 정책으로 관리되는가? <br> 

#### 2) 트랜잭션 
(1) Redis 분산락 생성 시, 자식 트랜잭션을 독립적으로 생성해주었다 <br>
-> 이 부분에 대해서 명확하게 이해했는가? <br> 
  
(2) 트랜잭션 범위를 조정하기 위한 여러 시도들을 했는데(@Transactional 위치 변경, 자식 트랜잭션 생성 등) <br>
    테스트가 제대로 되지 않는 문제가 발생해서, 결론적으로 철회했다. <br> 
-> 왜 그런 문제들이 발생했는가? 왜 트랜잭션 범위를 조정하는 것이 왜 잘 되지 않았는가? <br>  


#### 3) 테스트 
(1) 예약 하위에서 결제가 발생하고 있는데, 이에 대한 테스트를 분리해야 하는가? 아니면 예약에 대한 테스트만으로 충분한가? <br> 

(2) 데이터베이스 클렌징에 대해 제안해주신 코드 적용해보기  





