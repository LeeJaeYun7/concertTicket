package seatinfo.domain;

import concertschedule.domain.ConcertSchedule;
import global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seat.domain.Seat;
import seatgrade.domain.SeatGrade;
import seatinfo.domain.enums.SeatStatus;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "seat_info")
@NoArgsConstructor
public class SeatInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id")
    private SeatGrade seatGrade;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Builder
    public SeatInfo(Seat seat, ConcertSchedule concertSchedule, SeatGrade seatGrade, SeatStatus status) {
        this.seat = seat;
        this.concertSchedule = concertSchedule;
        this.seatGrade = seatGrade;
        this.status = status;
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public static SeatInfo of(Seat seat, ConcertSchedule concertSchedule, SeatGrade seatGrade, SeatStatus status){
        return SeatInfo.builder()
                .seat(seat)
                .concertSchedule(concertSchedule)
                .seatGrade(seatGrade)
                .status(status)
                .build();
    }

    public void updateStatus(SeatStatus status){
        this.status = status;
    }

    public void changeUpdatedAt(LocalDateTime dateTime){
        this.setUpdatedAt(dateTime);
    }
}