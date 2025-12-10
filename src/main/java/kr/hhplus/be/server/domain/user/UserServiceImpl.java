package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    public UserInfo.PointMain getPoint(Long userId) {
        User findUser = userRepository.findById(userId);
        return UserInfo.PointMain.of(findUser);
    }

    @Override
    public Long chargePoint(UserCommand.chargePoint command) {
        User findUser = userRepository.findById(command.userId());
        Long balance = findUser.chargePoint(command.amount());
        PointHistory pointHistory = PointHistory.snapShotOfCharge(findUser, command.amount());
        pointHistoryRepository.save(pointHistory);
        return balance;
    }

    @Override
    public Long usePoint(UserCommand.usePoint command) {
        User findUser = userRepository.findById(command.userId());
        Long balance = findUser.usePoint(command.amount());
        PointHistory pointHistory = PointHistory.snapShotOfUse(findUser, command.amount());
        pointHistoryRepository.save(pointHistory);
        return balance;
    }
}
