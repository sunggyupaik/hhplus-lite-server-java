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
import java.util.List;

import static kr.hhplus.be.server.domain.queue.WaitingToken.EXPIRED_MINUTE_FROM_CREATED_AT;
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

    @Nested
    @DisplayName("runQueueRefreshJob 메서드는")
    class Describe_of_runQueueRefreshJob {
        @Nested
        @DisplayName("일정한 주기마다")
        class Context_with_some_minutes {
            private static final Long USER_BALANCE_1000L = 1000L;
            private static final String WAITING_TOKEN_QWE = "QWE";
            private static final String WAITING_TOKEN_ABC = "ABC";
            private static final String WAITING_TOKEN_ZXC = "ZXC";
            private static final String WAITING_TOKEN_FGH = "FGH";
            private static final String WAITING_TOKEN_QWER = "QWER";
            private static final String WAITING_TOKEN_ASDF = "ASDF";
            private static final String WAITING_TOKEN_ZXCV = "ZXCV";
            User user_1000;
            List<WaitingToken> heldToExpired; // 만료 예정 토큰
            List<WaitingToken> waitingToHeld; // 활성화 예정 토큰

            @BeforeEach
            void prepare() {
                LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
                LocalDateTime nowTime_1m_before = nowTime.minusMinutes(1);
                LocalDateTime nowTime_2m_before = nowTime.minusMinutes(2);
                LocalDateTime nowTime_3m_before = nowTime.minusMinutes(3);
                LocalDateTime nowTime_10m_before = nowTime.minusMinutes(10);
                LocalDateTime nowTime_11m_before = nowTime.minusMinutes(11);
                LocalDateTime nowTime_12m_before = nowTime.minusMinutes(12);
                LocalDateTime nowTime_13m_before = nowTime.minusMinutes(13);
                user_1000 = createUser(USER_BALANCE_1000L);
                WaitingToken heldToExpired1 = createWaitingToken(user_1000.getId(), WaitingToken.Status.HELD, WAITING_TOKEN_QWE, nowTime_10m_before);
                WaitingToken heldToExpired2 = createWaitingToken(user_1000.getId(), WaitingToken.Status.HELD, WAITING_TOKEN_ABC, nowTime_11m_before);
                WaitingToken heldToExpired3 = createWaitingToken(user_1000.getId(), WaitingToken.Status.HELD, WAITING_TOKEN_ZXC, nowTime_12m_before);
                WaitingToken heldToExpired4 = createWaitingToken(user_1000.getId(), WaitingToken.Status.HELD, WAITING_TOKEN_FGH, nowTime_13m_before);
                heldToExpired = List.of(heldToExpired1, heldToExpired2, heldToExpired3, heldToExpired4);

                WaitingToken waitingToHeld1 = createWaitingToken(user_1000.getId(), WAITING_TOKEN_QWER, nowTime_1m_before);
                WaitingToken waitingToHeld2 = createWaitingToken(user_1000.getId(), WAITING_TOKEN_ASDF, nowTime_2m_before);
                WaitingToken waitingToHeld3 = createWaitingToken(user_1000.getId(), WAITING_TOKEN_ZXCV, nowTime_3m_before);
                waitingToHeld = List.of(waitingToHeld1, waitingToHeld2, waitingToHeld3);
            }

            @Test
            @DisplayName("만료시간이 지난 토큰을 만료하고 대기중 토큰을 활성화한다")
            void it_expires_expired_tokens_enqueues_new_users() {
                QueueInfo.QueueRefreshCountInfo queueRefreshCountInfo = queueService.runQueueRefreshJob();
                // 만료된 토큰
                int updateToExpiredTokenCount = queueRefreshCountInfo.updateToExpiredTokenCount();
                assertThat(updateToExpiredTokenCount).isEqualTo(heldToExpired.size());

                // 활성화된 토큰
                int updateToHeldTokenCount = queueRefreshCountInfo.updateToHeldTokenCount();
                assertThat(updateToHeldTokenCount).isEqualTo(waitingToHeld.size());
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
        return this.createWaitingToken(userId, WaitingToken.Status.WAITING, token, createdAt);
    }

    private WaitingToken createWaitingToken(Long userId, WaitingToken.Status status,
                                            String token, LocalDateTime createdAt) {
        WaitingToken waitingToken = WaitingToken.builder()
                .userId(userId)
                .token(token)
                .createdAt(createdAt)
                .expiredAt(createdAt.plusMinutes(EXPIRED_MINUTE_FROM_CREATED_AT))
                .status(status)
                .build();

        return queueJpaRepository.save(waitingToken);
    }

    private String LongToString(Long userId) {
        return String.valueOf(userId);
    }
}