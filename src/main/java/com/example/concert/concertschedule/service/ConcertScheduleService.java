package com.example.concert.concertschedule.service;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.repository.ConcertScheduleRepository;
import com.example.concert.utils.TimeProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertScheduleService {

    private final TimeProvider timeProvider;
    private final ConcertScheduleRepository concertScheduleRepository;

    public ConcertScheduleService(TimeProvider timeProvider, ConcertScheduleRepository concertScheduleRepository){
        this.timeProvider = timeProvider;
        this.concertScheduleRepository = concertScheduleRepository;
    }

    public List<ConcertSchedule> getAllConcertSchedulesAfterNowByConcertId(long concertId){
        LocalDateTime now = timeProvider.now();
        return concertScheduleRepository.findAllAfterNowByConcertId(concertId, now);
    }

    public ConcertSchedule getConcertScheduleById(long concertScheduleId) throws Exception {
        return concertScheduleRepository.findById(concertScheduleId)
                                        .orElseThrow(Exception::new);
    }
}
