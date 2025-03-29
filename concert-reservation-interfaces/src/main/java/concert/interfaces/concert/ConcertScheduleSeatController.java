package concert.interfaces.concert;

import concert.application.concert.business.ConcertScheduleSeatApplicationService;
import concert.domain.waitingqueue.entities.WaitingDTO;
import concert.interfaces.concert.request.ConcertScheduleSeatsRequest;
import concert.interfaces.concert.request.SeatNumbersRequest;
import concert.interfaces.concert.response.ConcertScheduleSeatsResponse;
import concert.interfaces.concert.response.SeatNumbersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConcertScheduleSeatController {
  private final ConcertScheduleSeatApplicationService concertScheduleSeatApplicationService;

  @GetMapping("/api/v1/concertScheduleSeat/active")
  public ResponseEntity<SeatNumbersResponse> retrieveActiveConcertScheduleSeats(@RequestBody SeatNumbersRequest seatNumbersRequest) {
    String token = seatNumbersRequest.getToken();
    WaitingDTO waitingDTO = WaitingDTO.parse(token);
    String uuid = waitingDTO.getUuid();
    long concertScheduleId = seatNumbersRequest.getConcertScheduleId();

    List<Long> activeSeatNumbers = concertScheduleSeatApplicationService.getActiveConcertScheduleSeatNumbers(uuid, concertScheduleId);
    SeatNumbersResponse seatNumbersResponse = new SeatNumbersResponse(activeSeatNumbers);

    return ResponseEntity.status(HttpStatus.OK).body(seatNumbersResponse);
  }

  @PostMapping("/api/v1/concertScheduleSeat/reservation")
  public ResponseEntity<ConcertScheduleSeatsResponse> reserveConcertScheduleSeats(@RequestBody ConcertScheduleSeatsRequest concertScheduleSeatsRequest) {
    String token = concertScheduleSeatsRequest.token();
    WaitingDTO waitingDTO = WaitingDTO.parse(token);
    String uuid = waitingDTO.getUuid();

    List<Long> concertScheduleSeatIds = concertScheduleSeatsRequest.concertScheduleSeatIds();

    concertScheduleSeatApplicationService.reserveConcertScheduleSeats(token, uuid, concertScheduleSeatIds);

    ConcertScheduleSeatsResponse response = new ConcertScheduleSeatsResponse(true);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
