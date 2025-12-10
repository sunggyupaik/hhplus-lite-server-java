package kr.hhplus.be.server.domain.concert;

import lombok.Builder;

public class SeatInfo {
    @Builder
    public record Main (
            Long seatId,
            Long scheduleId,
            String seatNumber,
            Long price,
            Seat.Status status
    ) {
        public static SeatInfo.Main of(Seat seat) {
            return SeatInfo.Main.builder()
                    .seatId(seat.getId())
                    .scheduleId(seat.getSchedule().getId())
                    .seatNumber(seat.getSeatNumber())
                    .price(seat.getPrice())
                    .status(seat.getStatus())
                    .build();
        }
    }
}
