package concert.interfaces.concert.request;

import java.time.LocalDateTime;

public record ConcertScheduleCreateRequest(String concertName, LocalDateTime dateTime) {
}
