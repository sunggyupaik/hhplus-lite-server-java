package kr.hhplus.be.server.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertServiceImpl implements ConcertService {
    private final ScheduleRepository scheduleRepository;

    @Override
    public List<ScheduleInfo.ConcertDate> schedulesFromToday() {
        LocalDate today = LocalDate.now();
        int todayInt = today.getYear() * 10000 + today.getMonthValue() * 100 + today.getDayOfMonth();
        List<Schedule> schedules = scheduleRepository.getScheduleDatesFromToday(todayInt);
        return schedules.stream()
                .map(schedule -> {
                    Integer concertDate = schedule.getConcertDate();
                    return new ScheduleInfo.ConcertDate(concertDate);
                })
                .collect(Collectors.toList());
    }
}
