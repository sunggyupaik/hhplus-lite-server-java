package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.domain.user.UserInfo;
import lombok.Builder;

public class UserDto {
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
}
