package kr.hhplus.be.server.infrastructure.concert;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findAllByScheduleConcertDates(Integer scheduleDate) {
        return seatJpaRepository.findAllByScheduleConcertDates(scheduleDate);
    }

    @Override
    public Seat findById(Long seatId) {
        return seatJpaRepository.findById(seatId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
