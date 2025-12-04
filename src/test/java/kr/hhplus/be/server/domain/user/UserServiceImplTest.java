package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Nested
    @DisplayName("chargePoint 메서드는")
    class Describe_of_chargePoint {
        private static final Long USER_BALANCE_1000L = 1000L;
        private static final Long CHARGE_POINT_1000L = 1000L;
        UserCommand.chargePoint command;
        User user_1000;
        @BeforeEach
        void prepare() {
            user_1000 = createUser(USER_BALANCE_1000L);
            command = UserCommand.chargePoint.builder()
                    .userId(user_1000.getId())
                    .amount(CHARGE_POINT_1000L)
                    .build();
        }

        @Nested
        @DisplayName("사용자 식별자와 충전금액이 주어진다면")
        class Context_with_user_id_and_amount {
            @Test
            @DisplayName("식별자에 해당하는 사용자의 포인트를 충전하고 결과를 리턴한다")
            void it_charges_point_returns_balance() {
                Long result = userService.chargePoint(command);
                assertThat(result).isEqualTo(USER_BALANCE_1000L + CHARGE_POINT_1000L);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 사용자 식별자가 주어진다면")
        class Context_with_not_existed_user_id {
            private static final Long NOT_EXISTED_USER_ID = 999L;
            UserCommand.chargePoint command;
            @BeforeEach
            void prepare() {
                command = UserCommand.chargePoint.builder()
                        .userId(NOT_EXISTED_USER_ID)
                        .amount(CHARGE_POINT_1000L)
                        .build();
            }

            @Test
            @DisplayName("존재하지 않는 사용자라는 예외를 던진다")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(
                        () -> userService.chargePoint(command)
                )
                        .isInstanceOf(EntityNotFoundException.class);
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