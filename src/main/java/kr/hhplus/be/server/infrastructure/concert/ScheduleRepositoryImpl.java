package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {
    private final ScheduleJpaRepository scheduleJpaRepository;

    @Override
    public List<Schedule> getScheduleDatesFromToday(int today) {
        return scheduleJpaRepository.getScheduleDatesFromToday(today);
    }
}
