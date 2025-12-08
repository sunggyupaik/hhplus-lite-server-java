package kr.hhplus.be.server.interfaces.reservation;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.reservation.Payment;
import kr.hhplus.be.server.domain.reservation.PaymentInfo;
import lombok.Builder;

public class ReservationDto {
    public record ReserveConcertRequest (
            @NotNull(message = "seatId는 필수입니다.")
            Long seatId,

            @NotNull(message = "scheduleDate는 필수입니다.")
            String scheduleDate
    ) {

    }

    public record payReservationRequest(
            @NotNull(message = "amount는 필수입니다.")
            Long amount,

            @NotNull(message = "idempotencyKey는 필수입니다.")
            String idempotencyKey
    ) {

    }

    @Builder
    public record ReserveResponse (
            Long reservationId
    ) {
        public static ReserveResponse of(Long createdId) {
            return ReserveResponse.builder()
                    .reservationId(createdId)
                    .build();
        }
    }

    @Builder
    public record PaymentResponse(
            Long paymentId,
            Long userId,
            Long seatId,
            Long amount,
            Payment.Status status
    ) {
        public static PaymentResponse of(PaymentInfo.Main result) {
            return PaymentResponse.builder()
                    .paymentId(result.paymentId())
                    .userId(result.userId())
                    .seatId(result.seatId())
                    .amount(result.amount())
                    .status(result.status())
                    .build();
        }
    }
}
