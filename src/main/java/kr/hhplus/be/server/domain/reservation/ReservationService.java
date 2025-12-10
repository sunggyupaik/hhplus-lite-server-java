package kr.hhplus.be.server.domain.reservation;

import java.time.LocalDateTime;

public interface ReservationService {
    Long reserveConcert(ReservationCommand.ReserveConcert command, LocalDateTime createdAt);

    PaymentInfo.Main payReservation(ReservationCommand.PayReservation command, LocalDateTime createdAt);
}
