package kr.hhplus.be.server.interfaces.reservation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.reservation.ReservationCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.support.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationApiController {
    private final ReservationService reservationService;

    @PostMapping
    public CommonResponse reserveConcert(@RequestHeader("X-Waiting-Token") String waitingToken,
                                         @RequestBody @Valid ReservationDto.ReserveConcertRequest request) {
        var reserveCommand = this.of(request);
        var createdId = reservationService.reserveConcert(reserveCommand, LocalDateTime.now());
        var response = ReservationDto.ReserveResponse.of(createdId);

        return CommonResponse.success(response);
    }

    private ReservationCommand.ReserveConcert of(ReservationDto.ReserveConcertRequest request) {
        return ReservationCommand.ReserveConcert.builder()
                .seatId(request.seatId())
                .concertDate(request.scheduleDate())
                .build();
    }
}
