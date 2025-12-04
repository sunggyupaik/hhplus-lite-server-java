package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.PointHistoryRepository;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public PointHistory save(PointHistory pointHistory) {
        validChk(pointHistory);
        return pointHistoryJpaRepository.save(pointHistory);
    }

    private void validChk(PointHistory pointHistory) {
        if (pointHistory.getAmount() == null) throw new InvalidParamException("PointHistory.amount");
        if (pointHistory.getType() == null) throw new InvalidParamException("PointHistory.type");
        if (pointHistory.getUser() == null) throw new InvalidParamException("PointHistory.user");
    }
}
