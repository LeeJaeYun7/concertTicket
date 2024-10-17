package com.example.concert.concert.fixtures;

import com.example.concert.concert.domain.Concert;
import org.springframework.util.ReflectionUtils;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class ConcertFixtureFactory {

    public static Concert createConcert(){
        return new Concert();
    }

    public static Concert createConcertWithIdAndName(long concertId, String name){
        Concert concert = createConcert();
        setField(concert, "id", concertId);
        setField(concert, "name", name);
        return concert;
    }
}
