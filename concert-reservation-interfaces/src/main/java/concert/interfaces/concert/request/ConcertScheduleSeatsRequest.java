package concert.interfaces.concert.request;

import java.util.List;

public record ConcertScheduleSeatsRequest(String token, List<Long> concertScheduleSeatIds) {
}
