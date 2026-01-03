package kr.hhplus.be.server.interfaces.reservation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.reservation.PaymentInfo;
import kr.hhplus.be.server.domain.reservation.ReservationCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.support.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationApiController {
    private final ReservationService reservationService;

    @PostMapping
    public CommonResponse reserveConcert(@RequestBody @Valid ReservationDto.ReserveConcertRequest request) {
        var reserveCommand = this.reserveCommandOf(request);
        var createdId = reservationService.reserveConcert(reserveCommand, LocalDateTime.now());
        var response = ReservationDto.ReserveResponse.of(createdId);

        return CommonResponse.success(response);
    }

    @PostMapping("/{reservationId}/payment")
    public CommonResponse payReservation(@PathVariable("reservationId") Long reservationId,
                                         @RequestBody @Valid ReservationDto.payReservationRequest request) {
        var paymentCommand = this.payReservationCommandOf(reservationId, request);
        PaymentInfo.Main paymentResult = reservationService.payReservation(paymentCommand, LocalDateTime.now());
        var response = ReservationDto.PaymentResponse.of(paymentResult);

        return CommonResponse.success(response);
    }

    private ReservationCommand.ReserveConcert reserveCommandOf(
            ReservationDto.ReserveConcertRequest request
    ) {
        return ReservationCommand.ReserveConcert.builder()
                .seatId(request.seatId())
                .concertDate(request.scheduleDate())
                .build();
    }

    private ReservationCommand.PayReservation payReservationCommandOf(
            Long reservationId,
            ReservationDto.payReservationRequest request
    ) {
        return ReservationCommand.PayReservation.builder()
                .reservationId(reservationId)
                .amount(request.amount())
                .idempotencyKey(request.idempotencyKey())
                .build();
    }
}
