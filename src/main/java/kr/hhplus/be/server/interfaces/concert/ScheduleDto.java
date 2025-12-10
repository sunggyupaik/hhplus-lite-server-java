package kr.hhplus.be.server.interfaces.concert;

import kr.hhplus.be.server.domain.concert.ScheduleInfo;
import lombok.Builder;

public class ScheduleDto {
    @Builder
    public record SchedulesConcertDate(
        Integer concertDate
    ) {
        public static SchedulesConcertDate of(ScheduleInfo.ConcertDate result) {
            return SchedulesConcertDate.builder()
                    .concertDate(result.concertDate())
                    .build();
        }
    }
}
