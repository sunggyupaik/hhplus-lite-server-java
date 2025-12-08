package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infrastructure.concert.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.PaymentHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.PaymentJpaRepository;
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.user.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.EntityNotFoundException;
import kr.hhplus.be.server.support.exception.IllegalStatusException;
import kr.hhplus.be.server.support.exception.InvalidParamException;
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
    @Autowired private ReservationJpaRepository reservationJpaRepository;
    @Autowired private ScheduleJpaRepository scheduleJpaRepository;
    @Autowired private ConcertJpaRepository concertJpaRepository;
    @Autowired private SeatJpaRepository seatJpaRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PointHistoryJpaRepository pointHistoryJpaRepository;
    @Autowired private PaymentHistoryJpaRepository paymentHistoryJpaRepository;
    @Autowired private PaymentJpaRepository paymentJpaRepository;

    @BeforeEach
    void setUp() {
        concertJpaRepository.deleteAll();
        scheduleJpaRepository.deleteAll();
        seatJpaRepository.deleteAll();
        reservationJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        pointHistoryJpaRepository.deleteAll();
        paymentJpaRepository.deleteAll();
        paymentHistoryJpaRepository.deleteAll();
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

    @Nested
    @DisplayName("payReservation 메서드는")
    class Describe_of_payReservation {
        private static final String IDEMPOTENCY_KEY_ABC = "ABC";
        private static final Long USER_BALANCE_20000L = 20000L;
        private static final Long PAY_AMOUNT_10000L = 10000L;
        private static final Long NOT_EXISTED_RESERVATION_ID = 99999L;
        User user;
        Concert concert;
        Schedule schedule_today;
        List<Seat> seats;
        Reservation reservation;
        ReservationCommand.PayReservation command;
        @BeforeEach
        void prepare() {
            user = createUser(USER_BALANCE_20000L);
            concert = createdConcert();

            LocalDateTime today = LocalDateTime.now();
            schedule_today = createdScheduleWithConcertDate(concert, today);
            seats = createdSeatsWithConcertDate(concert.getId(), schedule_today);
            changeSeatStatusToHeld(seats.get(0).getId());
            reservation = createdReservation(user.getId(), seats.get(0).getId(), today);
            command = createdPayReservationCommand(reservation.getId(), PAY_AMOUNT_10000L, IDEMPOTENCY_KEY_ABC);
        }

        @Nested
        @DisplayName("결제 정보가 주어진다면")
        class Context_with_paymentInfo {
            @Test
            @DisplayName("주어진 결제 정보로 결제를 하고 결과를 리턴한다")
            void it_payReservation_returns_result() {
                var result = reservationService.payReservation(command, LocalDateTime.now());

                Seat findSeat = seatJpaRepository.findById(result.seatId()).get();
                assertThat(findSeat.getStatus()).isEqualTo(Seat.Status.HELD);

                // 포인트 사용
                User findUser = userJpaRepository.findById(result.userId()).get();
                assertThat(findUser.getBalance()).isEqualTo(USER_BALANCE_20000L - command.amount());

                // 포인트 이력 생성
                assertThat(pointHistoryJpaRepository.count()).isEqualTo(1);

                // 결제완료
                Reservation findReservation = reservationJpaRepository.findById(command.reservationId()).get();
                assertThat(findReservation.getStatus()).isEqualTo(Reservation.Status.CONFIRMED);

                // 결제 이력 생성
                assertThat(paymentHistoryJpaRepository.count()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 예약 식별자가 주어진다면")
        class Context_with_not_existed_reservation_id {
            @BeforeEach
            void setUp() {
                command = createdPayReservationCommand(NOT_EXISTED_RESERVATION_ID, PAY_AMOUNT_10000L, IDEMPOTENCY_KEY_ABC);
            }

            @Test
            @DisplayName("엔티티가 존재하지 않는다는 예외를 던진다")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(
                        () -> reservationService.payReservation(command, LocalDateTime.now())
                )
                        .isInstanceOf(EntityNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("점유되지 않은 좌석이 주어진다면")
        class Context_with_not_held_seat {
            @BeforeEach
            void setUp() {
                command = createdPayReservationCommand(reservation.getId(), PAY_AMOUNT_10000L, IDEMPOTENCY_KEY_ABC);
                Seat findSeat = seats.get(0);
                findSeat.changeToAvailable();
                seatJpaRepository.save(findSeat);
            }

            @Test
            @DisplayName("상태가 올바르지 않다는 예외를 던진다")
            void it_throws_IllegalStateException() {

                assertThatThrownBy(
                        () -> reservationService.payReservation(command, LocalDateTime.now())
                )
                        .isInstanceOf(IllegalStatusException.class);
            }
        }

        @Nested
        @DisplayName("사용자의 포인트가 요청 금액보다 부족하다면")
        class Context_with_point_less_than_amount {
            @BeforeEach
            void setUp() {
                command = createdPayReservationCommand(
                        reservation.getId(), USER_BALANCE_20000L + PAY_AMOUNT_10000L, IDEMPOTENCY_KEY_ABC
                );
            }

            @Test
            @DisplayName("사용 가능한 포인트가 부족하다는 예외를 던진다")
            void it_throws_InvalidParamException() {
                assertThatThrownBy(
                        () -> reservationService.payReservation(command, LocalDateTime.now())
                )
                        .isInstanceOf(InvalidParamException.class);
            }
        }
    }

    private void changeSeatStatusToHeld(Long seatId) {
        Seat findSeat = seatJpaRepository.findById(seatId).get();
        findSeat.changeToHeld();
        seatJpaRepository.save(findSeat);
    }

    private ReservationCommand.PayReservation createdPayReservationCommand(Long reservationId, Long amount, String idempotencyKey) {
        return ReservationCommand.PayReservation.builder()
                .reservationId(reservationId)
                .amount(amount)
                .idempotencyKey(idempotencyKey)
                .build();
    }

    private ReservationCommand.ReserveConcert createdReserveConcertCommand(Long userId, Long seatId, String concertDate) {
        return ReservationCommand.ReserveConcert.builder()
                .userId(userId)
                .seatId(seatId)
                .concertDate(concertDate)
                .build();
    }

    private Reservation createdReservation(Long userId, Long seatId, LocalDateTime createdAt) {
        Reservation reservation = Reservation.builder()
                .userId(userId)
                .seatId(seatId)
                .createdAt(createdAt)
                .status(Reservation.Status.PENDING)
                .build();

        return reservationJpaRepository.save(reservation);
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
        return this.createUser(0L);
    }

    private User createUser(Long balance) {
        User user = User.builder()
                .email("email")
                .balance(balance)
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