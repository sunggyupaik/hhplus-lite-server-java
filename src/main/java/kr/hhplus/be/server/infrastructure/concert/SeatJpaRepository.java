package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s JOIN s.schedule sch WHERE sch.concertDate = :concertDate")
    List<Seat> findAllByScheduleConcertDates(@Param("concertDate") Integer concertDate);
}
