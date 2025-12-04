package kr.hhplus.be.server.interfaces.user;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.user.UserInfo;
import lombok.Builder;

public class UserDto {
    public record chargePointRequest(
            @NotNull(message = "amount는 필수입니다.")
            Long amount
    ) {

    }

    public record usePointRequest(
            @NotNull(message = "amount는 필수입니다.")
            Long amount
    ) {

    }

    @Builder
    public record PointMain(
            Long userId,
            Long balance
    ) {
        public static PointMain of(UserInfo.PointMain main) {
            return PointMain.builder()
                    .userId(main.userId())
                    .balance(main.balance())
                    .build();
        }
    }

    @Builder
    public record chargeResponse(
            Long balance
    ) {
        public static chargeResponse of(Long balance) {
            return chargeResponse.builder()
                    .balance(balance)
                    .build();
        }
    }

    @Builder
    public record useResponse(
            Long balance
    ) {
        public static useResponse of(Long balance) {
            return useResponse.builder()
                    .balance(balance)
                    .build();
        }
    }
}
