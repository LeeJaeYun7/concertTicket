
# MSA 기반 서비스 분리 시, Transactional Outbox Pattern 적용 보고서 

## 개요

이 보고서는 크게 5가지 파트로 구성됩니다.
  
**1) 문제 정의** <br>
**2) 기존 예약 기능** <br>
**3) 분산 트랜잭션 및 Saga 패턴** <br>
**4) MSA로 분리된 예약&결제 기능** <br>
**5) MSA 도입을 통해 개선된 점** <br> 


<br> 


**1) 문제 정의**
- 콘서트 티켓 예약 서비스에서는 예약 서버와 결제 서버를 **별도의 서버**로 분리하여 운영하고 있습니다. <br>
  사용자가 티켓을 예약하는 과정은 다음과 같은 흐름으로 진행됩니다 <br> 

(1) **예약 요청**: 사용자가 콘서트 티켓을 예약합니다. <br> 
(2) **결제 요청**: 예약이 완료되면 결제 서버에 결제 요청을 보냅니다. <br>
(3) **결제 완료**: 결제 서버에서 결제가 완료되면 해당 정보를 받습니다. <br>
(4) **예약 완료**: 결제가 완료된 후 최종적으로 예약이 확정됩니다. <br> 

- 이러한 예약 및 결제 과정에서, 예약 서버가 **예약 요청을 수신한 후** 결제 서버로 해당 이벤트를 전달해야 하는 상황이 발생합니다. <br>
  이를 위해 **Kafka**를 이벤트 발행 방식으로 활용하여, 예약 서버와 결제 서버 간의 데이터 전달을 구현하였습니다. <br>


- 그런데 이러한 이벤트 발행 시, Kafka 서버 다운 등으로 인한 예외 처리가 필요합니다. <br> 
  이러한 예외 케이스에 대해 코드로 살펴보겠습니다.


  ```





  ```