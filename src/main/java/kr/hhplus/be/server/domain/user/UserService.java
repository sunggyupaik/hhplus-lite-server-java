package kr.hhplus.be.server.domain.user;

public interface UserService {
    UserInfo.PointMain getPoint(Long userId);

    Long chargePoint(UserCommand.chargePoint command);
}
