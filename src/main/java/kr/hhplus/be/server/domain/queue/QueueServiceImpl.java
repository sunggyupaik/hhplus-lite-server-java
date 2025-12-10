package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.support.util.UserTokenRepository;
import kr.hhplus.be.server.support.util.WaitingTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {
    private final QueueRepository queueRepository;

    @Override
    public String issueToken(String userToken, LocalDateTime createdAt) {
        Long userId = UserTokenRepository.findUserId(userToken);
        String token = WaitingTokenGenerator.randomCharacter();
        WaitingToken waitingToken = WaitingToken.createFrom(userId, token, createdAt);

        WaitingToken createdWaitingToken = queueRepository.save(waitingToken);
        return createdWaitingToken.getToken();
    }

    @Override
    public QueueInfo.Main getStatus(String token) {
        return queueRepository.findByToken(token);
    }

    @Override
    @Scheduled(cron = "0 0/5 * * * *") // 5분마다 실행
    public QueueInfo.QueueRefreshCountInfo runQueueRefreshJob() {
        int updateToExpiredTokenCount = expireExpiredTokens();
        int updateToHeldTokenCount = enqueueNewUsers();
        return QueueInfo.QueueRefreshCountInfo.of(updateToExpiredTokenCount, updateToHeldTokenCount);
    }

    private int expireExpiredTokens() {
        return queueRepository.expireExpiredTokens();
    }

    private int enqueueNewUsers() {
        return queueRepository.enqueueNewUsers(WaitingToken.USER_COUNT_TO_ADD_QUEUE);
    }
}
