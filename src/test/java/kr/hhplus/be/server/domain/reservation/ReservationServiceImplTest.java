package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ReservationServiceImplTest {
    @Autowired private ReservationService reservationService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private ReservationJpaRepository reservationJpaRepository;
    @Autowired private ScheduleJpaRepository scheduleJpaRepository;
    @Autowired private ConcertJpaRepository concertJpaRepository;
    @Autowired private SeatJpaRepository seatJpaRepository;
    @Autowired private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        concertJpaRepository.deleteAll();
        scheduleJpaRepository.deleteAll();
        seatJpaRepository.deleteAll();
        reservationJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("reserveConcert 메서드는")
    class Describe_of_reserveConcert {
        ReservationCommand.ReserveConcert command;
        User user;
        Concert concert;
        Schedule schedule_today;
        List<Seat> seats;
        @BeforeEach
        void prepare() {
            user = createUser();
            concert = createdConcert();

            LocalDateTime today = LocalDateTime.now();
            schedule_today = createdScheduleWithConcertDate(concert, today);
            seats = createdSeatsWithConcertDate(concert.getId(), schedule_today);
            command = createdReserveConcertCommand(user.getId(), seats.get(0).getId(), localDateToString(today));
        }

        @Nested
        @DisplayName("콘서트 예약 정보가 주어진다면")
        class Context_with_reservationInfo {


            @Test
            @DisplayName("주어진 콘서트 예약 정보로 예약을 생성하고 식별자를 리턴한다")
            void it_creates_reservation_returns_id() {
                var reservationId = reservationService.reserveConcert(command, LocalDateTime.now());
                Reservation reservation = reservationJpaRepository.findById(reservationId).get();
                assertThat(reservation.getUserId()).isEqualTo(command.userId());
                assertThat(reservation.getSeatId()).isEqualTo(command.seatId());
            }
        }

        @Nested
        @DisplayName("이미 예약된 좌석을 또 예매하려고 하면")
        class Context_with_reservationInfo_already_held {
            ReservationCommand.ReserveConcert command;

            @BeforeEach
            void prepare() {
                for (var seat : seats) {
                    seat.changeToHeld();
                    seatJpaRepository.save(seat);
                }

                command = createdReserveConcertCommand(user.getId(), seats.get(0).getId(), localDateToString(LocalDateTime.now()));
            }

            @Test
            @DisplayName("예약을 할 수 없다는 예외를 리턴한다")
            void it_throws_illegal_status_exception() {
                assertThatThrownBy(
                        () -> reservationService.reserveConcert(command, LocalDateTime.now())
                )
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }

    private ReservationCommand.ReserveConcert createdReserveConcertCommand(Long userId, Long seatId, String concertDate) {
        return ReservationCommand.ReserveConcert.builder()
                .userId(userId)
                .seatId(seatId)
                .concertDate(concertDate)
                .build();
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

    private User createUser() {
        User user = User.builder()
                .email("email")
                .balance(0L)
                .build();

        return userJpaRepository.save(user);
    }

    private Integer localDateToInteger(LocalDateTime localDateTime) {
        return localDateTime.getYear() * 10000
                + localDateTime.getMonthValue() * 100
                + localDateTime.getDayOfMonth();
    }

    private String localDateToString(LocalDateTime localDateTime) {
        return Integer.toString(this.localDateToInteger(localDateTime));
    }
}