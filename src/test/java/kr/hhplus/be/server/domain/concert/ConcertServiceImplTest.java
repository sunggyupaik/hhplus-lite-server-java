package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ScheduleRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcertServiceImplTest {
    @Autowired private ConcertServiceImpl concertService;
    @Autowired private ScheduleRepositoryImpl scheduleRepositoryImpl;
    @Autowired private ScheduleJpaRepository scheduleJpaRepository;
    @Autowired private ConcertJpaRepository concertJpaRepository;

    @BeforeEach
    void setUp() {

    }

    @Nested
    @DisplayName("schedulesFromToday 메서드는")
    class Describe_of_schedulesFromToday {
        @Nested
        @DisplayName("항상")
        class Context_with_nothing {
            Concert concert;
            Schedule schedule_today, schedule_tomorrow, schedule_yesterday;
            @BeforeEach
            void prepare() {
                concert = createdConcert();

                LocalDateTime today = LocalDateTime.now();
                schedule_today = createdScheduleWithConcertDate(concert, today);
                schedule_tomorrow = createdScheduleWithConcertDate(concert, today.plusDays(1));
                schedule_yesterday = createdScheduleWithConcertDate(concert, today.minusDays(1));
            }

            @Test
            @DisplayName("오늘 기준으로 신청 가능한 콘서트 스케쥴을 리턴한다")
            void it_returns_schedules_from_today() {
                var concertDates = concertService.schedulesFromToday();
                for (var concertDate : concertDates) {
                    assertThat(concertDate.concertDate())
                            .isGreaterThanOrEqualTo(localDateToInteger(LocalDateTime.now()));
                }
            }
        }
    }

    private Concert createdConcert() {
        Concert concert = Concert.builder()
                .title("쇼미더머니")
                .description("랩")
                .ageLimitStatus(Concert.AgeLimitStatus.FIFTEEN)
                .build();

        return concertJpaRepository.save(concert);
    }

    private Schedule createdScheduleWithConcertDate(Concert concert, LocalDateTime concertDate) {
        Schedule schedule = Schedule.builder()
                .concert(concert)
                .basePrice(1000L)
                .concertDate(localDateToInteger(concertDate))
                .startTime("1200")
                .endTime("1400")
                .build();

        return scheduleJpaRepository.save(schedule);
    }

    private Integer localDateToInteger(LocalDateTime localDateTime) {
        return localDateTime.getYear() * 10000
                + localDateTime.getMonthValue() * 100
                + localDateTime.getDayOfMonth();
    }
}