package concert.interfaces.order;

import concert.application.order.business.OrderApplicationService;
import concert.interfaces.order.request.OrderRequest;
import concert.interfaces.order.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

  private final OrderApplicationService orderApplicationService;

  @PostMapping("/api/v1/order")
  public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
    String uuid = orderRequest.getUuid();
    long concertScheduleId = orderRequest.getConcertScheduleId();
    List<Long> concertScheduleSeatIds = orderRequest.getConcertScheduleSeatIds();

    orderApplicationService.createOrder(uuid, concertScheduleId, concertScheduleSeatIds);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
