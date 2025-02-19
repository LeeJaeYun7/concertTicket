package concert.interfaces.concert;

import concert.application.concert.business.ConcertScheduleSeatApplicationService;
import concert.interfaces.concert.request.ConcertScheduleSeatsRequest;
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
  public ResponseEntity<SeatNumbersResponse> retrieveActiveConcertScheduleSeats(@RequestParam(value = "concertScheduleId") long concertScheduleId) {
    List<Long> activeSeatNumbers = concertScheduleSeatApplicationService.getActiveConcertScheduleSeatNumbers(concertScheduleId);
    SeatNumbersResponse seatNumbersResponse = new SeatNumbersResponse(activeSeatNumbers);

    return ResponseEntity.status(HttpStatus.OK).body(seatNumbersResponse);
  }

  @PostMapping("/api/v1/concertScheduleSeat/reservation")
  public ResponseEntity<ConcertScheduleSeatsResponse> reserveConcertScheduleSeats(@RequestBody ConcertScheduleSeatsRequest concertScheduleSeatsRequest) {
    String token = concertScheduleSeatsRequest.token();
    List<Long> concertScheduleSeatIds = concertScheduleSeatsRequest.concertScheduleSeatIds();

    concertScheduleSeatApplicationService.reserveConcertScheduleSeats(token, concertScheduleSeatIds);

    ConcertScheduleSeatsResponse response = new ConcertScheduleSeatsResponse(true);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
