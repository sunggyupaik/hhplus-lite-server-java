package kr.hhplus.be.server.infrastructure.reservation;


import kr.hhplus.be.server.domain.reservation.PaymentHistory;
import kr.hhplus.be.server.domain.reservation.PaymentHistoryRepository;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepository {
    private final PaymentHistoryJpaRepository paymentHistoryJpaRepository;


    @Override
    public PaymentHistory save(PaymentHistory paymentHistory) {
        validChk(paymentHistory);
        return paymentHistoryJpaRepository.save(paymentHistory);
    }

    private void validChk(PaymentHistory paymentHistory) {
        if (paymentHistory.getReservationId() == null) throw new InvalidParamException("PaymentHistory.reservationId");
        if (paymentHistory.getAmount() == null) throw new InvalidParamException("PaymentHistory.amount");
        if (paymentHistory.getIdempotencyKey() == null) throw new InvalidParamException("PaymentHistory.idempotencyKey");
        if (paymentHistory.getCreatedAt() == null) throw new InvalidParamException("PaymentHistory.createdAt");
        if (paymentHistory.getStatus() == null) throw new InvalidParamException("PaymentHistory.STATUS");
    }
}
