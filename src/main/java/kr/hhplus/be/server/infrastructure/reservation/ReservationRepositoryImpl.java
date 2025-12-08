package kr.hhplus.be.server.infrastructure.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.support.exception.EntityNotFoundException;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        validCheck(reservation);
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Reservation findById(Long id) {
        return reservationJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id = " + id));
    }

    private void validCheck(Reservation reservation) {
        if (reservation.getUserId() == null) throw new InvalidParamException("reservation.userId");
        if (reservation.getSeatId() == null) throw new InvalidParamException("reservation.seatId");
        if (reservation.getCreatedAt() == null) throw new InvalidParamException("reservation.createdAt");
        if (reservation.getStatus() == null) throw new InvalidParamException("reservation.status");
    }
}
