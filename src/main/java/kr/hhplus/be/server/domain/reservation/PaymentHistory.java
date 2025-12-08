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
@Table(name="payment_histories")
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_history_id")
    private Long id;
    private Long reservationId;
    private Long amount;
    private String idempotencyKey;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        WAITING("결제대기"),
        PAID("결제완료");

        private final String description;
    }

    @Builder
    public PaymentHistory(Long id, Long reservationId, Long amount,
                          String idempotencyKey, LocalDateTime createdAt, Status status) {
        this.id = id;
        this.reservationId = reservationId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static PaymentHistory snapshotOf(Payment payment) {
        return PaymentHistory.builder()
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount())
                .idempotencyKey(payment.getIdempotencyKey())
                .createdAt(payment.getCreatedAt())
                .status(Status.valueOf(payment.getStatus().name()))
                .build();
    }
}
