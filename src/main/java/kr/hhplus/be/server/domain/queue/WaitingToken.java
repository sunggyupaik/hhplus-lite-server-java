package kr.hhplus.be.server.domain.queue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@ToString
@Table(name="waiting_tokens")
public class WaitingToken {
    public static final Long EXPIRED_MINUTE_FROM_CREATED_AT = 5L;
    public static final Long USER_COUNT_TO_ADD_QUEUE = 10L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_token_id")
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        WAITING("대기중"),
        HELD("점유중"),
        EXPIRED("만료경과");

        private final String description;
    }

    @Builder
    public WaitingToken(Long id, Long userId, String token, LocalDateTime createdAt,
                        LocalDateTime expiredAt, Status status) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.status = status;
    }

    public static WaitingToken createFrom(Long userId, String token, LocalDateTime createdAt) {
        return WaitingToken.builder()
                .userId(userId)
                .token(token)
                .createdAt(createdAt)
                .expiredAt(createdAt.plusMinutes(EXPIRED_MINUTE_FROM_CREATED_AT))
                .status(WaitingToken.Status.WAITING)
                .build();
    }
}
