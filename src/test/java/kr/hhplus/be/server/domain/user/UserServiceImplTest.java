package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.infrastructure.user.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.EntityNotFoundException;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceImplTest {
    @Autowired private UserService userService;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PointHistoryJpaRepository pointHistoryJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
        pointHistoryJpaRepository.deleteAll();
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

                List<PointHistory> pointHistory = pointHistoryJpaRepository.findAll();
                assertThat(pointHistory.get(0).getAmount()).isEqualTo(CHARGE_POINT_1000L);
                assertThat(pointHistory.get(0).getType()).isEqualTo(PointHistory.Type.CHARGE);
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

    @Nested
    @DisplayName("usePoint 메서드는")
    class Describe_of_usePoint {
        private static final Long USER_BALANCE_2000L = 2000L;
        private static final Long USE_POINT_1000L = 1000L;
        UserCommand.usePoint command;
        User user_2000;
        @BeforeEach
        void prepare() {
            user_2000 = createUser(USER_BALANCE_2000L);
            command = UserCommand.usePoint.builder()
                    .userId(user_2000.getId())
                    .amount(USE_POINT_1000L)
                    .build();
        }

        @Nested
        @DisplayName("사용자 식별자와 사용 할 금액이 주어진다면")
        class Context_with_user_id_and_amount {
            @Test
            @DisplayName("식별자에 해당하는 사용자의 포인트를 차감하고 결과를 리턴한다")
            void it_uses_point_returns_balance() {
                Long result = userService.usePoint(command);
                assertThat(result).isEqualTo(USER_BALANCE_2000L - USE_POINT_1000L);

                List<PointHistory> pointHistory = pointHistoryJpaRepository.findAll();
                assertThat(pointHistory.get(0).getAmount()).isEqualTo(USE_POINT_1000L);
                assertThat(pointHistory.get(0).getType()).isEqualTo(PointHistory.Type.USE);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 사용자 식별자가 주어진다면")
        class Context_with_not_existed_user_id {
            private static final Long NOT_EXISTED_USER_ID = 999L;
            UserCommand.usePoint command;
            @BeforeEach
            void prepare() {
                command = UserCommand.usePoint.builder()
                        .userId(NOT_EXISTED_USER_ID)
                        .amount(USE_POINT_1000L)
                        .build();
            }

            @Test
            @DisplayName("존재하지 않는 사용자라는 예외를 던진다")
            void it_throws_EntityNotFoundException() {
                assertThatThrownBy(
                        () -> userService.usePoint(command)
                )
                        .isInstanceOf(EntityNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("사용 후 금액이 0원보다 작으면")
        class Context_with_amount_more_than_balance {
            private static final Long USE_POINT_3000L = 3000L;
            UserCommand.usePoint command;
            @BeforeEach
            void prepare() {
                command = UserCommand.usePoint.builder()
                        .userId(user_2000.getId())
                        .amount(USE_POINT_3000L)
                        .build();
            }

            @Test
            @DisplayName("잘못된 요청이라는 예외를 던진다")
            void it_throws_InvalidParamException() {
                assertThatThrownBy(
                        () -> userService.usePoint(command)
                )
                        .isInstanceOf(InvalidParamException.class);
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