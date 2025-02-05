package concert.interfaces.concert.response;

import java.time.LocalDateTime;
import java.util.List;

public record ActiveConcertSchedulesResponse(List<LocalDateTime> availableConcertSchedules) {
}
