package kr.hhplus.be.server.domain.reservation;

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
@Table(name="reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;
    private Long userId;
    private Long seatId;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        PENDING("대기중"),
        CONFIRMED("결제완료"),
        EXPIRED("만료경과");

        private final String description;
    }

    @Builder
    public Reservation(Long id, Long userId, Long seatId, LocalDateTime createdAt, Status status) {
        this.id = id;
        this.userId = userId;
        this.seatId = seatId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public void changeStatusToConfirmed() {
        this.status = Status.CONFIRMED;
    }
}
