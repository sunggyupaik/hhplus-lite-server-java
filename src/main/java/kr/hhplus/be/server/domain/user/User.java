package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@ToString
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String email;
    private Long balance;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @ToString.Exclude
    private List<PointHistory> pointHistoryList = new ArrayList<>();

    @Builder
    public User(Long id, String email, Long balance) {
        this.id = id;
        this.email = email;
        this.balance = balance;
    }

    public Long chargePoint(Long amount) {
        this.balance += amount;
        return this.balance;
    }

    public Long usePoint(Long amount) {
        if (this.balance - amount < 0) {
            throw new InvalidParamException("사용 가능한 포인트가 부족합니다");
        }

        this.balance -= amount;
        return this.balance;
    }
}
