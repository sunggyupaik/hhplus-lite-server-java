package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT c FROM Schedule c WHERE c.concertDate >= :today")
    List<Schedule> getScheduleDatesFromToday(@Param("today") int today);
}
