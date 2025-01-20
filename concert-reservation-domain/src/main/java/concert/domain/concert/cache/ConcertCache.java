package concert.domain.concert.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import concert.domain.concert.domain.Concert;

import java.util.List;

public interface ConcertCache {
    void saveTop30Concerts(List<Concert> concerts) throws JsonProcessingException, JsonProcessingException;
    List<Concert> findTop30Concerts() throws JsonProcessingException;
}
