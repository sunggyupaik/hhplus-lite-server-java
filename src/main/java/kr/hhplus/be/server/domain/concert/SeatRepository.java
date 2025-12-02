package kr.hhplus.be.server.domain.concert;

import java.util.List;

public interface SeatRepository {
    List<Seat> findAllByScheduleConcertDates(Integer scheduleDate);
}
