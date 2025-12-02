package kr.hhplus.be.server.domain.concert;

import java.util.List;

public interface ConcertService {
    List<ScheduleInfo.ConcertDate> schedulesFromToday();

    List<SeatInfo.Main> seatsFrom(String scheduleDate);
}
