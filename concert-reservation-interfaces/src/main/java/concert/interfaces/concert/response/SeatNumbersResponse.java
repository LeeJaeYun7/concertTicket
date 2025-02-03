package concert.interfaces.concert.response;

import java.util.List;

public record SeatNumbersResponse(List<Long> availableSeatNumbers) {
}