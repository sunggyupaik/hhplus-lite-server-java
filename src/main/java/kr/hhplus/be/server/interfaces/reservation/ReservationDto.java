package kr.hhplus.be.server.interfaces.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ReservationDto {
    public record ReserveConcertRequest (
            @NotNull(message = "seatId는 필수입니다.")
            Long seatId,

            @NotNull(message = "scheduleDate는 필수입니다.")
            String scheduleDate
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
}
