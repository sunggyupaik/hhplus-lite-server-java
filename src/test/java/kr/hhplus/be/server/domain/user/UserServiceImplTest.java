package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceImplTest {
    @Autowired private UserService userService;
    @Autowired private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("getPoint 메서드는")
    class Describe_of_getPoint {
        @Nested
        @DisplayName("사용자 식별자가 주어진다면")
        class Context_with_user_id {
            private static final Long USER_BALANCE_1000L = 1000L;
            User user_1000;
            @BeforeEach
            void prepare() {
                user_1000 = createUser(USER_BALANCE_1000L);
            }

            @Test
            @DisplayName("식별자에 해당하는 사용자의 포인트 정보를 리턴한다")
            void it_returns_point_info() {
                UserInfo.PointMain pointInfo = userService.getPoint(user_1000.getId());
                assertThat(pointInfo.balance()).isEqualTo(USER_BALANCE_1000L);
            }
        }
    }

    private User createUser(Long balance) {
        User user = User.builder()
                .email("email")
                .balance(balance)
                .build();

        return userJpaRepository.save(user);
    }
}