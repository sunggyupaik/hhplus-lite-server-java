package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserInfo.PointMain getPoint(Long userId) {
        User findUser = userRepository.findById(userId);
        return UserInfo.PointMain.of(findUser);
    }

    @Override
    public Long chargePoint(UserCommand.chargePoint command) {
        User findUser = userRepository.findById(command.userId());
        return findUser.chargePoint(command.amount());
    }
}
