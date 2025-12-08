package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatRepository;
import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.exception.IllegalStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    public Long reserveConcert(ReservationCommand.ReserveConcert command, LocalDateTime createdAt) {
        Seat seat = seatRepository.findById(command.seatId());
        if (seat.getStatus() != Seat.Status.AVAILABLE) throw new IllegalStateException();

        var entity = command.toEntity(createdAt);
        Reservation createdReservation = reservationRepository.save(entity);
        return createdReservation.getId();
    }

    @Override
    @Transactional
    public PaymentInfo.Main payReservation(ReservationCommand.PayReservation command, LocalDateTime createdAt) {
        // 예약 유효성 검증 + 결제완료 상태 변경
        Reservation reservation = reservationRepository.findById(command.reservationId());
        reservation.changeStatusToConfirmed();

        // 좌석 점유 상태 검증
        Seat seat = seatRepository.findById(reservation.getSeatId());
        if (seat.getStatus() != Seat.Status.HELD) throw new IllegalStatusException();

        // 포인트사용 및 이력 생성
        User user = userRepository.findById(reservation.getUserId());
        user.usePoint(command.amount());
        PointHistory pointHistory = PointHistory.snapShotOfUse(user, command.amount());
        pointHistoryRepository.save(pointHistory);

        // 결제 생성 및 이력 생성
        Payment entity = command.toEntity(createdAt);
        Payment createdPayment = paymentRepository.save(entity);
        PaymentHistory paymentHistory = PaymentHistory.snapshotOf(createdPayment);
        paymentHistoryRepository.save(paymentHistory);
        return PaymentInfo.Main.of(createdPayment, reservation);
    }
}
