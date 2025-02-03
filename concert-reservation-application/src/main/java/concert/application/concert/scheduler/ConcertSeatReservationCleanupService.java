package concert.application.concert.scheduler;

import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.entities.dao.ConcertScheduleSeatEntityDAO;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertSeatReservationCleanupService {

    private final ConcertScheduleSeatEntityDAO concertScheduleSeatEntityDAO;

    @Scheduled(fixedRate = 5000) // 5초마다 실행
    @Transactional
    public void restoreConcertScheduleSeatReservations() {
        try {
            LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
            List<ConcertScheduleSeatEntity> concertScheduleSeats =
                    concertScheduleSeatEntityDAO.updateExpiredConcertScheduleSeats(ConcertScheduleSeatStatus.PENDING, threshold);

            concertScheduleSeats.forEach(seat -> seat.updateStatus(ConcertScheduleSeatStatus.AVAILABLE));
            concertScheduleSeatEntityDAO.saveAll(concertScheduleSeats);

            log.info("Restored ConcertScheduleSeatReservations");
        } catch (Exception e) {
            log.error("An exception occurred while restoring concert seat reservations", e);
        }
    }
}
