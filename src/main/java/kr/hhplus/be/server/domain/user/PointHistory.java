package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@ToString
@Table(name="point_histories")
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long id;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        CHARGE("충전"),
        USE("사용");

        private final String description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public PointHistory(Long id, Long amount, Type type, User user) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.user = user;
    }

    public static PointHistory snapShotOfCharge(User user, Long amount) {
        return PointHistory.builder()
                .amount(amount)
                .type(Type.CHARGE)
                .user(user)
                .build();
    }

    public static PointHistory snapShotOfUse(User user, Long amount) {
        return PointHistory.builder()
                .amount(amount)
                .type(Type.USE)
                .user(user)
                .build();
    }
}
