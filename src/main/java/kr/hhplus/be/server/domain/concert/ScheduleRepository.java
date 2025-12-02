package kr.hhplus.be.server.domain.concert;

import java.util.List;

public interface ScheduleRepository {
    List<Schedule> getScheduleDatesFromToday(int today);
}
