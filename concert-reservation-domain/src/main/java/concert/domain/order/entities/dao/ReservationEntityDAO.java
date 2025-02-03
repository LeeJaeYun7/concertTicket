package concert.domain.order.entities.dao;

import concert.domain.concert.entities.ConcertEntity;
import concert.domain.order.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationEntityDAO extends JpaRepository<ReservationEntity, Long> {

  // 최근 3일 간 콘서트 티켓 예약량 기준으로 Top30을 반환
  @Query("SELECT c, r.salesCount " +
          "FROM ConcertEntity c " +
          "JOIN (SELECT r.concertId AS concertId, COUNT(*) AS salesCount " +
          "FROM ReservationEntity r " +
          "WHERE r.createdAt >= :threeDaysAgo " +
          "GROUP BY r.concertId) r " +
          "ON c.id = r.concertId " +
          "ORDER BY r.salesCount DESC")
  List<ConcertEntity> findTop30Concerts(@Param("threeDaysAgo") LocalDateTime threeDaysAgo);
}
