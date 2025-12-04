package kr.hhplus.be.server.domain.user;

import lombok.Builder;

public class UserCommand {
    @Builder
    public record chargePoint(
            Long userId,
            Long amount
    ) {

    }
}
