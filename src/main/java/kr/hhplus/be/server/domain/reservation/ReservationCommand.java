package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

import java.time.LocalDateTime;

public class ReservationCommand {
    @Builder
    public record ReserveConcert (
            Long userId,
            Long seatId,
            String concertDate
    ) {
        public Reservation toEntity(LocalDateTime createdAt) {
            return Reservation.builder()
                    .userId(userId)
                    .seatId(seatId)
                    .createdAt(createdAt)
                    .status(Reservation.Status.PENDING)
                    .build();
        }
    }

    @Builder
    public record PayReservation (
            Long reservationId,
            Long amount,
            String idempotencyKey
    ) {

        public Payment toEntity(LocalDateTime createdAt) {
            return Payment.builder()
                    .reservationId(reservationId)
                    .amount(amount)
                    .idempotencyKey(idempotencyKey)
                    .createdAt(createdAt)
                    .status(Payment.Status.PAID)
                    .build();
        }
    }
}
