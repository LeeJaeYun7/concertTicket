package concert.interfaces.concert.request;

import java.util.List;

public record ConcertScheduleSeatsRequest(List<Long> concertScheduleSeatIds) {
}
