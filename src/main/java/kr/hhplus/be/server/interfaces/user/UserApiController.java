package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.domain.user.UserInfo;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.support.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        UserInfo.PointMain pointsInfo = userService.getPoint(userId);
        var response = UserDto.PointMain.of(pointsInfo);

        return CommonResponse.success(response);
    }
}
