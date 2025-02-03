package concert.interfaces.concert.response;

import java.time.LocalDateTime;
import java.util.List;

public record AvailableDateTimesResponse(List<LocalDateTime> availableDateTimes) {
}
