package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.support.util.UserTokenRepository;
import kr.hhplus.be.server.support.util.WaitingTokenGenerator;
import lombok.RequiredArgsConstructor;
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
        WaitingToken waitingToken = WaitingToken.builder()
                .userId(userId)
                .waitingToken(token)
                .createdAt(createdAt)
                .expiredAt(createdAt.plusHours(1))
                .status(WaitingToken.Status.WAITING)
                .build();

        WaitingToken createdWaitingToken = queueRepository.save(waitingToken);
        return createdWaitingToken.getWaitingToken();
    }
}
