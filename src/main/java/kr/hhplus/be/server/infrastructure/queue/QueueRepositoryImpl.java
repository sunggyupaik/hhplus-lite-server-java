package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.QueueInfo;
import kr.hhplus.be.server.domain.queue.QueueRepository;
import kr.hhplus.be.server.domain.queue.WaitingToken;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {
    private final QueueJpaRepository queueJpaRepository;

    @Override
    public WaitingToken save(WaitingToken waitingToken) {
        validChk(waitingToken);
        return queueJpaRepository.save(waitingToken);
    }

    @Override
    public QueueInfo.Main findByToken(String waitingToken) {
        QueueInfo.MainIF findMainIF = queueJpaRepository.findByWaitingToken(waitingToken);
        return QueueInfo.Main.of(findMainIF);
    }

    private void validChk(WaitingToken waitingToken) {
        if (waitingToken.getUserId() == null) throw new InvalidParamException("waitingToken.userId");
        if (waitingToken.getToken() == null) throw new InvalidParamException("waitingToken.waitingToken");
        if (waitingToken.getCreatedAt() == null) throw new InvalidParamException("waitingToken.createdAt");
        if (waitingToken.getExpiredAt() == null) throw new InvalidParamException("waitingToken.expiredAt");
        if (waitingToken.getStatus() == null) throw new InvalidParamException("waitingToken.status");
    }
}
