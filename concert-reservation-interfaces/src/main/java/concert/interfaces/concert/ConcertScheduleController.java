package concert.interfaces.concert;

import com.fasterxml.jackson.core.JsonProcessingException;
import concert.application.concert.business.ConcertScheduleApplicationService;
import concert.interfaces.concert.request.ConcertScheduleCreateRequest;
import concert.interfaces.concert.response.ActiveConcertSchedulesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConcertScheduleController {

  private final ConcertScheduleApplicationService concertScheduleApplicationService;

  @PostMapping("/api/v1/concertSchedule")
  public ResponseEntity<Void> createConcertSchedule(@RequestBody ConcertScheduleCreateRequest concertScheduleCreateRequest) throws JsonProcessingException {
    String concertName = concertScheduleCreateRequest.concertName();
    LocalDateTime dateTime = concertScheduleCreateRequest.dateTime();

    concertScheduleApplicationService.createConcertSchedule(concertName, dateTime);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/api/v1/concertSchedule")
  public ResponseEntity<ActiveConcertSchedulesResponse> retrieveActiveConcertSchedules(@RequestParam(value = "concertId") long concertId) {
    List<LocalDateTime> availableDateTimes = concertScheduleApplicationService.getActiveConcertSchedules(concertId);
    ActiveConcertSchedulesResponse activeConcertSchedules = new ActiveConcertSchedulesResponse(availableDateTimes);

    return ResponseEntity.status(HttpStatus.OK).body(activeConcertSchedules);
  }
}
