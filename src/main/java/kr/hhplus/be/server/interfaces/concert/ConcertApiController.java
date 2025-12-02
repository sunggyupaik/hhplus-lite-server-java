package kr.hhplus.be.server.interfaces.concert;

import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.support.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/concerts")
public class ConcertApiController {
    private final ConcertService concertService;

    @GetMapping("/schedules")
    public CommonResponse schedulesFromToday(@RequestHeader("X-Waiting-Token") String waitingToken) {
        var concertSchedules = concertService.schedulesFromToday();
        var response = concertSchedules.stream()
                .map(ScheduleDto.SchedulesConcertDate::of)
                .collect(Collectors.toList());

        return CommonResponse.success(response);
    }

    @GetMapping("/seats")
    public CommonResponse seats(@RequestHeader("X-Waiting-Token") String waitingToken,
                                @RequestParam(name = "schedule-date") String scheduleDate) {
        var concertSchedules = concertService.seatsFrom(scheduleDate);
        var response = concertSchedules.stream()
                .map(SeatDto.Main::of)
                .collect(Collectors.toList());

        return CommonResponse.success(response);
    }
}
