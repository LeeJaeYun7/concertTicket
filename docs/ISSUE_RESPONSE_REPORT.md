# API 부하 테스트 분석과 가상 장애 대응 방안에 관한 보고서



## 1. 개별 API 테스트

- 각 API에 대해 개별적으로 부하 테스트를 실시했다. <br> 
 

### (1) 잔액 충전 API 테스트

```
import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정
export let options = {
  stages: [
      { duration: '60s', target: 1000 }, // 60초 동안 1000명의 가상 사용자가 요청을 보냄
  ],
  thresholds: {
      http_req_duration: ['p(99)<1000'], // 99%의 요청이 1000ms 이내에 처리되어야 함
      http_req_failed: ['rate<0.01'], // 실패율이 1% 미만이어야 함
  },
  summaryTrendStats: ['avg', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  // 요청할 URL
  let url = 'http://localhost:8080/api/v1/charge';

  // ChargeRequest DTO에 맞는 JSON 데이터
  let payload = JSON.stringify({
    uuid: 'user-1234',     // UUID 예시값 (테스트용 고유값)
    amount: 1000           // 충전 금액 예시값
  });

  // HTTP POST 요청 보내기
  let params = {
    headers: {
      'Content-Type': 'application/json',  // JSON 형식으로 전송
    },
  };

  let response = http.post(url, payload, params);

  // 응답이 null이 아니고, 상태 코드가 200이어야 한다
  check(response, {
    'status is 200': (r) => r.status === 200,
  });

  // 요청 간 간격을 주기 위한 sleep
  sleep(1);
}
```

![image](https://github.com/user-attachments/assets/612abf8e-d737-4d05-a101-e23fe07c2530)


#### (1) 분석

##### (1-1) 처리량
= 초당 약 145개의 요청을 처리했다.
- 총 9490개의 요청이 처리되었다.

##### (1-2) 응답 시간
- 평균 응답 시간: 2.33s
- 90번째 백분위 응답 시간: 4.92s
- 95번째 백분위 응답 시간: 5.98s
- 에러율: 0%로, 모든 요청이 성공적으로 처리되었다.

##### (1-3) 동시 사용자
- 동시 사용자: 최대 1,000명의 가상 사용자(VU)가 동시에 테스트를 수행했다.

##### (1-4) 네트워크 지연
- 요청 차단 시간(avg): 4.03ms
- 연결 시간(avg): 2.82ms


#### (2) 테스트 분석 결론
- 평균 응답 시간이 2.33s로 다소 느린 편이다.
- 95번째 백분위 응답 시간이 5.98s로 매우 높아, 일부 요청에서 지연이 발생할 수 있다.
- 시스템이 초당 145개의 요청을 처리할 수 있다. 초당 처리량의 개선이 필요하다.




### (2) 잔액 조회 API 테스트

```
import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정
export let options = {
  stages: [
      { duration: '60s', target: 1000 }, // 60초 동안 1000명의 가상 사용자가 요청을 보냄
  ],
  thresholds: {
      http_req_duration: ['p(99)<1000'], // 99%의 요청이 1000ms 이내에 처리되어야 함
      http_req_failed: ['rate<0.01'], // 실패율이 1% 미만이어야 함
  },
  summaryTrendStats: ['avg', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  // 요청할 URL
  let url = 'http://localhost:8080/api/v1/balance';

  // HTTP POST 요청 보내기
  let params = {
    headers: {
      'Content-Type': 'application/json',  // JSON 형식으로 전송
    },
  };

  let response = http.get(url, params);

  // 응답이 null이 아니고, 상태 코드가 200이어야 한다
  check(response, {
    'status is 200': (r) => r.status === 200,
  });

  // 요청 간 간격을 주기 위한 sleep
  sleep(1);
}
```

![image](https://github.com/user-attachments/assets/0c28b034-738e-44f4-a360-9bdab10f1ada)


#### (1) 분석

##### (1-1) 처리량
= 초당 약 420개의 요청을 처리했다.
- 총 25678개의 요청이 처리되었다.

##### (1-2) 응답 시간
- 평균 응답 시간: 184.89ms
- 90번째 백분위 응답 시간: 202.97ms
- 95번째 백분위 응답 시간: 1.99s
- 에러율: 0%로, 모든 요청이 성공적으로 처리되었다.

##### (1-3) 동시 사용자
- 동시 사용자: 최대 1,000명의 가상 사용자(VU)가 동시에 테스트를 수행했다.

##### (1-4) 네트워크 지연
- 요청 차단 시간(avg): 133.65us
- 연결 시간(avg): 94.27us

#### (2) 테스트 분석 결론
- 평균 응답 시간이 184.89ms로 빠른 편이다.
- 95번째 백분위 응답 시간이 1.99s로 매우 높아, 일부 요청에서 지연이 발생할 수 있다.
- 시스템이 초당 420개의 요청을 처리했다. 




### (3) 예약 가능한 콘서트 조회 API 테스트

```
import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정
export let options = {
  stages: [
      { duration: '60s', target: 1000 }, // 60초 동안 1000명의 가상 사용자가 요청을 보냄
  ],
  thresholds: {
      http_req_duration: ['p(99)<1000'], // 99%의 요청이 1000ms 이내에 처리되어야 함
      http_req_failed: ['rate<0.01'], // 실패율이 1% 미만이어야 함
  },
  summaryTrendStats: ['avg', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  // 요청할 URL
  let url = 'http://localhost:8080/api/v1/concertSchedule';

  // HTTP POST 요청 보내기
  let params = {
    headers: {
      'Content-Type': 'application/json',  // JSON 형식으로 전송
    },
  };

  let response = http.get(url, params);

  // 응답이 null이 아니고, 상태 코드가 200이어야 한다
  check(response, {
    'status is 200': (r) => r.status === 200,
  });

  // 요청 간 간격을 주기 위한 sleep
  sleep(1);
}
```

![image](https://github.com/user-attachments/assets/0192ca52-7e1c-4d67-bb6f-6b06cc59d28a)


#### (1) 분석

##### (1-1) 처리량
= 초당 약 360개의 요청을 처리했다.
  총 22007개의 요청이 처리되었다.

##### (1-2) 응답 시간
- 평균 응답 시간: 286.15ms
- 90번째 백분위 응답 시간: 314.97ms
- 95번째 백분위 응답 시간: 3.24s
- 에러율: 0%로, 모든 요청이 성공적으로 처리되었다.


##### (1-3) 동시 사용자
- 동시 사용자: 최대 1,000명의 가상 사용자(VU)가 동시에 테스트를 수행했다.

##### (1-4) 네트워크 지연
- 요청 차단 시간(avg): 13.07ms
- 연결 시간(avg): 10.11ms

#### (2) 테스트 분석 결론
- 평균 응답 시간이 286.15ms로 빠른 편이다.
- 95번째 백분위 응답 시간이 3.24s로 매우 높아, 일부 요청에서 지연이 발생할 수 있다.
- 시스템이 초당 360개의 요청을 처리했다. 




### (4) 예약 API 테스트

```
import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정
export let options = {
  stages: [
      { duration: '60s', target: 1000 }, // 60초 동안 1000명의 가상 사용자가 요청을 보냄
  ],
  thresholds: {
      http_req_duration: ['p(99)<1000'], // 99%의 요청이 1000ms 이내에 처리되어야 함
      http_req_failed: ['rate<0.01'], // 실패율이 1% 미만이어야 함
  },
  summaryTrendStats: ['avg', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  // 요청할 URL
  let url = 'http://localhost:8080/api/v1/reservation';

  // POST 요청에 보낼 JSON 데이터
  let payload = JSON.stringify({
    uuid: '123e4567-e89b-12d3-a456-426614174000', // UUID 예시
    concertScheduleId: 101,  // 공연 일정 ID 예시
    seatNumber: 25  // 좌석 번호 예시
  });

  // HTTP POST 요청 보내기
  let params = {
    headers: {
      'Content-Type': 'application/json',  // JSON 형식으로 전송
    },
  };

  // 실제 POST 요청
  let response = http.post(url, payload, params);

  // 응답이 200이어야 한다는 검증
  check(response, {
    'status is 200': (r) => r.status === 200,
  });

  // 요청 간 간격을 주기 위한 sleep
  sleep(1);
}
```

![image](https://github.com/user-attachments/assets/516a5f61-ae74-4f2b-99b7-e3a00c127710)


#### (1) 분석

##### (1-1) 처리량
= 초당 약 74개의 요청을 처리했다.
  총 6084개의 요청이 처리되었다.

##### (1-2) 응답 시간
- 평균 응답 시간: 4.9s
- 90번째 백분위 응답 시간: 7.83s
- 95번째 백분위 응답 시간: 10.84s
- 에러율: 0%로, 모든 요청이 성공적으로 처리되었다.


##### (1-3) 동시 사용자
- 동시 사용자: 최대 1,000명의 가상 사용자(VU)가 동시에 테스트를 수행했다.

##### (1-4) 네트워크 지연
- 요청 차단 시간(avg): 1.76ms
- 연결 시간(avg): 1.72ms


#### (2) 테스트 분석 결론
- 평균 응답 시간이 4.9s로 느린 편이다.
- 95번째 백분위 응답 시간이 10.84s로 매우 높아, 일부 요청에서 지연이 발생할 수 있다.
- 시스템이 초당 74개의 요청을 처리했다.


<br> 

### (5) 대기열 토큰 발급 테스트

```
import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정
export let options = {
  stages: [
    { duration: '60s', target: 1000 }, // 60초 동안 1000명의 가상 사용자가 요청을 보냄
  ],
  thresholds: {
    http_req_duration: ['p(99)<1000'], // 99%의 요청이 1000ms 이내에 처리되어야 함
    http_req_failed: ['rate<0.01'], // 실패율이 1% 미만이어야 함
  },
  summaryTrendStats: ['avg', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  // retrieveToken API 테스트
  let concertId = 101; // 공연 ID 예시
  let uuid = 'f00338f1-3a0e-4d1b-94d8-3a8ba14bbe36'; // UUID 예시
  let url = `http://localhost:8080/api/v1/waitingQueue/token?concertId=${concertId}&uuid=${uuid}`;
  
  // GET 요청 보내기
  let response = http.get(url);
  
  // 응답이 201이어야 한다는 검증
  check(response, {
    'status is 201': (r) => r.status === 201,
    'body contains token': (r) => r.body.includes('token'), // 응답에 'token' 키가 있어야 함
  });

  // 요청 간 간격을 주기 위한 sleep
  sleep(1);
}

```

![image](https://github.com/user-attachments/assets/261ded8a-c025-4b79-a7d8-f5f6d8601b59)

#### (1) 분석

##### (1-1) 처리량
= 초당 약 111개의 요청을 처리했다.
  총 7825개의 요청이 처리되었다.

##### (1-2) 응답 시간
- 평균 응답 시간: 3.53s
- 90번째 백분위 응답 시간: 6.98s
- 95번째 백분위 응답 시간: 8.85s
- 에러율: 0%로, 모든 요청이 성공적으로 처리되었다.


##### (1-3) 동시 사용자
- 동시 사용자: 최대 1,000명의 가상 사용자(VU)가 동시에 테스트를 수행했다.

##### (1-4) 네트워크 지연
- 요청 차단 시간(avg): 412.35µs
- 연결 시간(avg): 344.23µs 


#### (2) 테스트 분석 결론
- 평균 응답 시간이 3.53s로 느린 편이다.
- 95번째 백분위 응답 시간이 6.98s로 높아, 일부 요청에서 지연이 발생할 수 있다.
- 시스템이 초당 111개의 요청을 처리했다.




### (6) 대기열 대기 번호 발급 테스트

```
import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정
export let options = {
  stages: [
    { duration: '60s', target: 1000 }, // 60초 동안 1000명의 가상 사용자가 요청을 보냄
  ],
  thresholds: {
    http_req_duration: ['p(99)<1000'], // 99%의 요청이 1000ms 이내에 처리되어야 함
    http_req_failed: ['rate<0.01'], // 실패율이 1% 미만이어야 함
  },
  summaryTrendStats: ['avg', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  // retrieveWaitingRank API 테스트
  let concertId = 101; // 공연 ID 예시
  let token = '21732810616808:f00338f1-3a0e-4d1b-94d8-3a8ba14bbe36'; // 'retrieveToken'에서 받은 token 예시
  let url = `http://localhost:8080/api/v1/waitingQueue/rank?concertId=${concertId}&token=${token}`;
  
  // GET 요청 보내기
  let response = http.get(url);
  
  // 응답이 201이어야 한다는 검증
  check(response, {
    'status is 200': (r) => r.status === 200,
    'body contains rank': (r) => r.body.includes('rank'), // 응답에 'rank' 키가 있어야 함
  });

  // 요청 간 간격을 주기 위한 sleep
  sleep(1);
}
```

![image](https://github.com/user-attachments/assets/2685f500-1bad-42c5-8547-e2a02adad5b3)


#### (1) 분석

##### (1-1) 처리량
= 초당 약 101개의 요청을 처리했다.
  총 6948개의 요청이 처리되었다.

##### (1-2) 응답 시간
- 평균 응답 시간: 3.86s
- 90번째 백분위 응답 시간: 6.59s
- 95번째 백분위 응답 시간: 7.58s
- 에러율: 0%로, 모든 요청이 성공적으로 처리되었다.


##### (1-3) 동시 사용자
- 동시 사용자: 최대 1,000명의 가상 사용자(VU)가 동시에 테스트를 수행했다.

##### (1-4) 네트워크 지연
- 요청 차단 시간(avg): 3.56ms
- 연결 시간(avg): 1.9ms 


#### (2) 테스트 분석 결론
- 평균 응답 시간이 3.86s로 느린 편이다.
- 95번째 백분위 응답 시간이 6.59s로 높아, 일부 요청에서 지연이 발생할 수 있다.
- 시스템이 초당 101개의 요청을 처리했다.





## 2. 종합 평가 및 결론

- 대부분의 테스트가 thresholds를 만족시키지 못해서 이에 대한 개선이 필요해 보입니다.
-> 이는 테스트를 local 서버에서 진행한 영향도가 있어 보입니다.
  
  thresholds: {
      http_req_duration: ['p(99)<1000'], // 99%의 요청이 1000ms 이내에 처리되어야 함
      http_req_failed: ['rate<0.01'], // 실패율이 1% 미만이어야 함
  }

- 예약 API 테스트 같은 경우는, 현재 스크립트로는 같은 좌석에 대한 중복 예약이 발생할 수 있어서,
  테스트 스크립트의 수정이 필요합니다. 

- 로컬 환경에서 테스트 했기 때문에, 도커+Grafana를 활용한 테스트가 필요합니다. 



# 2. 가상 장애 대응 방안에 관한 보고서

## 1. 개요

- 본 문서는 콘서트 예매 시스템의 부하 테스트 결과를 바탕으로, 발생 가능한 다양한 장애 상황과 그에 대한 대응 방안을 상세히 기술한다. 


# 2. 잠재적 장애 시나리오 및 대응 방안

### 2.1 데이터베이스 과부하

#### 시나리오:
- 콘서트 예매 오픈 직후, 동시에 수만 명의 사용자가 예약을 시도하면서 데이터베이스 서버의 CPU 사용률이 95%를 초과하고, 쿼리 응답 시간이 평균 500ms 이상으로 증가하는 상황이다. 
- 이로 인해 전체 시스템의 응답 속도가 현저히 저하되고, 일부 사용자는 예매 페이지에 접속조차 할 수 없는 상태를 가정한다.

#### 징후:
- 데이터베이스 서버 CPU 사용률 95% 초과 지속 (5분 이상)
- 쿼리 응답 시간 500ms 이상 (정상 시 평균 50ms)

#### 대응 방안:

1. **즉시 조치:**
    - 읽기 전용 쿼리를 읽기 전용 복제본으로 리다이렉션한다.
        - 방법: 애플리케이션의 데이터베이스 연결 설정을 동적으로 변경하여 읽기 쿼리를 복제본으로 전송한다.
        - 효과: 주 데이터베이스 서버의 부하를 약 40-50% 감소시킬 것으로 예상된다.
    - 커넥션 풀 크기를 조정한다 (현재 값의 1.5배로 증가).
        - 방법: WAS의 데이터베이스 커넥션 풀 설정을 실시간으로 조정한다.
        - 효과: 동시 처리 가능한 쿼리 수가 증가하고, 대기 시간이 감소한다.
    
2. **단기 조치:**
    - 데이터베이스 서버 리소스를 증설한다 (CPU, 메모리).
        - 방법: 클라우드 환경의 경우 인스턴스 타입을 변경하고, 물리 서버의 경우 긴급 하드웨어를 증설한다.
        - 목표: CPU 코어 수 2배 증가, 메모리 1.5배 증가
    - 인덱스를 재구성하고 쿼리를 최적화한다.
        - 방법: 실행 계획 분석을 통해 비효율적인 쿼리를 식별하고 최적화한다.
        - 목표: 주요 쿼리의 실행 시간 30% 이상 단축
    
3. **장기 조치:**
    - 데이터베이스 샤딩을 구현한다.
        - 방법: 사용자 ID나 공연 ID를 기준으로 데이터를 여러 데이터베이스에 분산 저장한다.
        - 목표: 단일 데이터베이스 서버의 부하를 분산하여 전체 처리량 3배 이상 증가
    - 읽기/쓰기 분리 아키텍처를 도입한다.
        - 방법: 쓰기 작업은 마스터 DB로, 읽기 작업은 다수의 읽기 전용 복제본으로 분산한다.
        - 목표: 읽기 작업의 처리량 5배 이상 증가, 쓰기 작업의 안정성 확보

<br>

### 2.2 캐시 서버 장애

#### 시나리오:
- Redis 캐시 서버가 갑자기 다운되어, 콘서트 및 좌석 정보 조회 API의 응답 시간이 급격히 증가하는 상황이다. 
- 평균 응답 시간이 50ms에서 2000ms로 증가하고, 시스템 전반의 성능이 저하된다. 

#### 징후:
- Redis 연결 오류 로그가 1초당 100건 이상 발생한다.
- API 응답 시간이 평균 2000ms 이상으로 증가한다 (정상 시 50ms).
- 데이터베이스 부하가 갑자기 200% 이상 증가한다.
- 실시간 모니터링 대시보드에서 캐시 히트율이 0%로 떨어진다.

#### 대응 방안:

1. **즉시 조치**
    - 백업 캐시 서버로 자동 전환한다 (미리 구성된 경우).
        - 방법: DNS 또는 로드 밸런서 설정을 통해 트래픽을 백업 캐시 서버로 리다이렉트한다.
        - 목표: 5분 이내에 캐시 서비스 복구
    - 애플리케이션의 캐시 우회 로직을 활성화한다.
        - 방법: 미리 준비된 환경 변수 또는 설정을 통해 캐시 없이 직접 DB 조회하도록 전환한다.
        - 목표: 서비스의 기본적인 기능 유지, 응답 시간을 1000ms 이내로 단축
    - 사용자에게 일시적인 서비스 지연을 안내한다.
        - 방법: 웹사이트 배너 및 푸시 알림을 통해 현재 상황과 예상 복구 시간을 안내한다.
        - 목표: 고객 불만 최소화 및 과도한 재시도 방지

2. **단기 조치:**
    - Redis 서버를 재시작하고 상태를 확인한다.
        - 방법: 서버 로그 분석 후 Redis 프로세스를 재시작하고, 메모리 및 연결 상태를 점검한다.
        - 목표: 근본 원인 파악 및 서비스 안정화
    - 캐시 데이터를 재구축한다.
        - 방법: 주요 데이터(공연 정보, 좌석 상태 등)에 대한 캐시 워밍업 스크립트를 실행한다.
        - 목표: 30분 이내에 캐시 히트율 90% 이상 회복
    - 장애 원인을 분석한다 (메모리 부족, 네트워크 이슈 등).
        - 방법: 시스템 로그, 네트워크 트래픽 로그, Redis 슬로우 로그 등을 종합적으로 분석한다.
        - 목표: 재발 방지를 위한 명확한 원인 파악 및 문서화

3. **장기 조치**
    - Redis 클러스터를 구성하여 고가용성을 확보한다.
        - 방법: 최소 3대의 마스터 노드와 각각의 슬레이브 노드로 클러스터를 구성한다.
        - 목표: 단일 노드 장애 시에도 서비스 중단 없이 운영 가능한 환경 구축
    - 캐시 데이터 분산 저장 전략을 수립한다.
        - 방법: 데이터 특성에 따라 여러 Redis 인스턴스에 분산 저장하는 로직을 구현한다.
        - 목표: 캐시 서버 부하 분산 및 전체 처리량 3배 이상 증가

<br>

### 2.3 대기열 시스템 오작동

#### 시나리오:
- Redis 로 구현한 대기열 관리 시스템에 버그가 발생하여, 사용자들이 무작위로 대기열에서 튕겨나가거나, 대기 순서가 뒤바뀌는 현상이 발생한다. 
- 이로 인해 예매 시작 10분 만에 고객 불만이 급증한다.
  
#### 징후:
- 고객 지원 센터로의 문의가 평소의 10배 수준으로 폭주한다 (분당 200건 이상).
- 대기열 시스템 로그에서 비정상적인 패턴이 발견된다 (순서 역전, 갑작스러운 세션 종료 등).
- 실시간 모니터링에서 대기열 이탈률이 정상치의 5배 이상으로 증가한다.


#### 대응 방안:

1. **즉시 조치**
    - 대기열 시스템을 일시적으로 정적 대기열로 전환한다.
        - 방법: 미리 준비된 정적 HTML 페이지로 대기열 페이지를 대체한다.
        - 목표: 추가적인 시스템 오류 방지 및 상황 안정화
    - 모든 사용자에게 현재 상황과 예상 대기 시간을 안내한다.
        - 방법: 팝업 메시지, SMS, 이메일을 통해 일괄 안내한다.
        - 목표: 사용자 불안 감소 및 무분별한 재접속 시도 방지

2. **단기 조치**
    - 버그 원인을 파악하고 긴급 패치를 적용한다.
        - 방법: 핫픽스 배포를 수행한다.
        - 목표: 근본적인 문제 해결 및 시스템 안정화
    - 영향받은 사용자들에게 보상 정책을 수립하고 안내한다.
        - 방법: 예매 우선권 부여, 할인 쿠폰 제공 등의 보상책을 마련한다.
        - 목표: 고객 신뢰 회복 및 부정적 여론 완화
    - 대기열 데이터를 복구하고 정상화한다.
        - 방법: 백업 데이터를 활용하여 사용자의 원래 대기 순서를 복원한다.
        - 목표: 공정성 논란 해소 및 시스템 신뢰도 회복

3. **장기 조치**
    - 스트레스 테스트 및 시뮬레이션을 강화한다.
        - 방법: 다양한 장애 시나리오에 대한 정기적인 모의훈련을 실시한다.
        - 목표: 유사 상황 재발 방지 및 대응 능력 향상
    - 실시간 모니터링 및 알림 시스템을 고도화한다.
        - 방법: 실시간 모니터링 시스템과 이상 징후 탐지 시스템을 도입한다.
        - 목표: 장애 조기 감지 및 선제적 대응 체계 구축

<br>
