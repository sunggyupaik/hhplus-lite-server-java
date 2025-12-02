package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ScheduleRepositoryImpl;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcertServiceImplTest {
    @Autowired private ConcertServiceImpl concertService;
    @Autowired private ScheduleRepositoryImpl scheduleRepositoryImpl;
    @Autowired private ScheduleJpaRepository scheduleJpaRepository;
    @Autowired private ConcertJpaRepository concertJpaRepository;
    @Autowired private SeatJpaRepository seatJpaRepository;

    @BeforeEach
    void setUp() {
        concertJpaRepository.deleteAll();
        scheduleJpaRepository.deleteAll();
        seatJpaRepository.deleteAll();
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

    @Nested
    @DisplayName("seats 메서드는")
    class Describe_of_seats {
        @Nested
        @DisplayName("콘서트 날짜가 주어진다면")
        class Context_with_concert_date {
            Concert concert;
            Schedule schedule_today;
            List<Seat> seats;
            @BeforeEach
            void prepare() {
                concert = createdConcert();

                LocalDateTime today = LocalDateTime.now();
                schedule_today = createdScheduleWithConcertDate(concert, today);
                seats = createdSeatsWithConcertDate(concert.getId(), schedule_today);
            }

            @Test
            @DisplayName("날짜에 해당하는 콘서트 좌석 목록을 리턴한다")
            void it_returns_seats_from_concert_date() {
                assertThat(seats.size()).isEqualTo(50);
                var seats = concertService.seatsFrom(Integer.toString(localDateToInteger(LocalDateTime.now())));
                for (var seat : seats) {
                    assertThat(seat.status()).isEqualTo(Seat.Status.AVAILABLE);
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

    private List<Seat> createdSeatsWithConcertDate(Long concertId, Schedule schedule) {
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            Seat seat = Seat.builder()
                    .concertId(concertId)
                    .seatNumber(Integer.toString(i))
                    .price(1000L)
                    .status(Seat.Status.AVAILABLE)
                    .schedule(schedule)
                    .build();

            Seat createdSeat = seatJpaRepository.save(seat);
            seats.add(createdSeat);
        }

        return seats;
    }

    private Integer localDateToInteger(LocalDateTime localDateTime) {
        return localDateTime.getYear() * 10000
                + localDateTime.getMonthValue() * 100
                + localDateTime.getDayOfMonth();
    }
}