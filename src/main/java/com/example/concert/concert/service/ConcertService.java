package com.example.concert.concert.service;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.repository.ConcertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository){
        this.concertRepository = concertRepository;
    }

    public Concert getConcertById(long concertId) throws Exception {
        Optional<Concert> concertOpt = concertRepository.findById(concertId);

        if(concertOpt.isEmpty()){
            throw new Exception();
        }

        return concertOpt.get();
    }

    public List<Long> getAllConcertIds() throws Exception {
        return concertRepository.findAll().stream().map(Concert::getId).collect(Collectors.toList());
    }
}
