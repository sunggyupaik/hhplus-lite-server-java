package kr.hhplus.be.server.interfaces.concert;

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatInfo;
import lombok.Builder;

public class SeatDto {
    @Builder
    public record Main (
            Long seatId,
            Long scheduleId,
            String seatNumber,
            Long price,
            Seat.Status status
    ) {
        public static SeatInfo.Main of(SeatInfo.Main result) {
            return SeatInfo.Main.builder()
                    .seatId(result.seatId())
                    .scheduleId(result.scheduleId())
                    .seatNumber(result.seatNumber())
                    .price(result.price())
                    .status(result.status())
                    .build();
        }
    }
}
