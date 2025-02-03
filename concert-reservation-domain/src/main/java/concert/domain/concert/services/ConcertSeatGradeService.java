package concert.domain.concert.services;

import concert.domain.concert.entities.ConcertSeatGradeEntity;
import concert.domain.concert.entities.dao.ConcertSeatGradeEntityDAO;
import concert.domain.concert.exceptions.ConcertException;
import concert.domain.concert.exceptions.ConcertExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertSeatGradeService {

    private final ConcertSeatGradeEntityDAO concertSeatGradeEntityDAO;

    public long getConcertSeatGradePrice(long seatGradeId){
        ConcertSeatGradeEntity seatGrade = concertSeatGradeEntityDAO.findById(seatGradeId).orElseThrow(() -> new ConcertException(ConcertExceptionType.CONCERT_SEAT_GRADE_NOT_FOUND));
        return seatGrade.getPrice();
    }
}
