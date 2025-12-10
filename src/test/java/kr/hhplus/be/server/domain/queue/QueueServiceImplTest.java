package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.infrastructure.queue.QueueJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class QueueServiceImplTest {
    @Autowired QueueService queueService;
    @Autowired UserJpaRepository userJpaRepository;
    @Autowired QueueJpaRepository queueJpaRepository;

    @BeforeEach
    void setUp() {
        queueJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("issueToken 메서드는")
    class Describe_of_issueToken {
        @Nested
        @DisplayName("사용자 토큰이 주어지지 않는다면")
        class Context_with_not_userToken {
            @Test
            @DisplayName("잘못된 요청이라는 예외를 던진다")
            void it_throws_InvalidParamException() {
                assertThatThrownBy(
                        () -> queueService.issueToken(null, LocalDateTime.now())
                )
                        .isInstanceOf(InvalidParamException.class);
            }
        }

        @Nested
        @DisplayName("사용자 토큰이 주어진다면")
        class Context_with_userToken {
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

            @Test
            @DisplayName("주어진 사용자 식별자에 해당하는 토큰을 만든다")
            void it_issue_tokens_returns_token() {
                String createdToken = queueService.issueToken(LongToString(user_1000.getId()), LocalDateTime.now());
                assertThat(createdToken.length()).isEqualTo(32);
            }
        }
    }

    @Nested
    @DisplayName("getStatus 메서드는")
    class Describe_of_getStatus {
        @Nested
        @DisplayName("존재하는 대기열 토큰이 주어진다면")
        class Context_with_existed_waitingToken {
            private static final Long USER_BALANCE_1000L = 1000L;
            private static final String WAITING_TOKEN_QWE = "QWE";
            private static final String WAITING_TOKEN_ABC = "ABC";
            private static final String WAITING_TOKEN_ZXC = "ZXC";
            private static final String WAITING_TOKEN_FGH = "FGH";
            User user_1000;

            @BeforeEach
            void prepare() {
                LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
                LocalDateTime nowTime_1m_before = nowTime.minusMinutes(1);
                LocalDateTime nowTime_2m_before = nowTime.minusMinutes(2);
                LocalDateTime nowTime_3m_before = nowTime.minusMinutes(3);
                LocalDateTime nowTime_4m_before = nowTime.minusMinutes(4);
                user_1000 = createUser(USER_BALANCE_1000L);
                createWaitingToken(user_1000.getId(), WAITING_TOKEN_QWE, nowTime_1m_before);
                createWaitingToken(user_1000.getId(), WAITING_TOKEN_ABC, nowTime_2m_before);
                createWaitingToken(user_1000.getId(), WAITING_TOKEN_ZXC, nowTime_3m_before);
                createWaitingToken(user_1000.getId(), WAITING_TOKEN_FGH, nowTime_4m_before);
            }

            @Test
            @DisplayName("대기열 토큰에 해당하는 토큰 정보를 리턴한다")
            void it_returns_waitingTokenInfo() {
                QueueInfo.Main status = queueService.getStatus(WAITING_TOKEN_ZXC);

                assertThat(status.waitingNumber()).isEqualTo(2);
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

    private WaitingToken createWaitingToken(Long userId, String token, LocalDateTime createdAt) {
        WaitingToken waitingToken = WaitingToken.createFrom(userId, token, createdAt);

        return queueJpaRepository.save(waitingToken);
    }

    private String LongToString(Long userId) {
        return String.valueOf(userId);
    }
}