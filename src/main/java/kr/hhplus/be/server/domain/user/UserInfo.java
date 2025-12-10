package kr.hhplus.be.server.domain.user;

import lombok.Builder;

public class UserInfo {
    @Builder
    public record PointMain(
            Long userId,
            Long balance
    ) {
        public static PointMain of(User findUser) {
            return PointMain.builder()
                    .userId(findUser.getId())
                    .balance(findUser.getBalance())
                    .build();
        }
    }
}
