package kr.hhplus.be.server.interfaces.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.support.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;

    @GetMapping("/{userId}/points")
    public CommonResponse getPoints(@RequestHeader("X-Waiting-Token") String waitingToken,
                                    @PathVariable("userId") Long userId) {
        var pointsInfo = userService.getPoint(userId);
        var response = UserDto.PointMain.of(pointsInfo);

        return CommonResponse.success(response);
    }

    @PostMapping("/{userId}/points")
    public CommonResponse chargePoint(@RequestHeader("X-Waiting-Token") String waitingToken,
                                      @PathVariable("userId") Long userId,
                                      @RequestBody @Valid UserDto.chargePointRequest request) {
        UserCommand.chargePoint command = this.chargePointOf(userId, request);
        var balance = userService.chargePoint(command);
        var response = UserDto.chargeResponse.of(balance);

        return CommonResponse.success(response);
    }

    private UserCommand.chargePoint chargePointOf(Long userId, UserDto.chargePointRequest request) {
        return UserCommand.chargePoint.builder()
                .userId(userId)
                .amount(request.amount())
                .build();
    }
}
