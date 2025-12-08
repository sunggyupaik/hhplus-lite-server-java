package kr.hhplus.be.server.infrastructure.reservation;

import kr.hhplus.be.server.domain.reservation.Payment;
import kr.hhplus.be.server.domain.reservation.PaymentRepository;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        validCheck(payment);
        return paymentJpaRepository.save(payment);
    }

    private void validCheck(Payment payment) {
        if (payment.getReservationId() == null) throw new InvalidParamException("Payment.reservationId");
        if (payment.getAmount() == null) throw new InvalidParamException("Payment.amount");
        if (payment.getIdempotencyKey() == null) throw new InvalidParamException("Payment.idempotencyKey");
        if (payment.getCreatedAt() == null) throw new InvalidParamException("Payment.createdAt");
        if (payment.getStatus() == null) throw new InvalidParamException("Payment.STATUS");
    }
}
