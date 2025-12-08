package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

public class PaymentInfo {
    @Builder
    public record Main(
            Long paymentId,
            Long userId,
            Long seatId,
            Long amount,
            Payment.Status status
    ) {
        public static Main of(Payment payment, Reservation reservation) {
            return Main.builder()
                    .paymentId(payment.getId())
                    .userId(reservation.getUserId())
                    .seatId(reservation.getSeatId())
                    .amount(payment.getAmount())
                    .status(payment.getStatus())
                    .build();
        }
    }
}
