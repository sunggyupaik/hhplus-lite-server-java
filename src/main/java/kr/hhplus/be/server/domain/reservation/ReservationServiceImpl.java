package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Override
    public Long reserveConcert(ReservationCommand.ReserveConcert command, LocalDateTime createdAt) {
        Seat seat = seatRepository.findById(command.seatId());
        if (seat.getStatus() != Seat.Status.AVAILABLE) throw new IllegalStateException();

        var entity = command.toEntity(createdAt);
        Reservation createdReservation = reservationRepository.save(entity);
        return createdReservation.getId();
    }
}
