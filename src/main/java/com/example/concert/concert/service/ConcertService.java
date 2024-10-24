package com.example.concert.concert.service;

import com.example.concert.common.CustomException;
import com.example.concert.common.ErrorCode;
import com.example.concert.common.Loggable;
import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.repository.ConcertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository){
        this.concertRepository = concertRepository;
    }

    public Concert getConcertById(long concertId) {
        return concertRepository.findById(concertId)
                                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND, Loggable.ALWAYS));
    }

    public List<Long> getAllConcertIds() {
        return concertRepository.findAll().stream().map(Concert::getId).collect(Collectors.toList());
    }
}
