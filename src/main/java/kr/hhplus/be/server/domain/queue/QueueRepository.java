package kr.hhplus.be.server.domain.queue;

public interface QueueRepository {
    WaitingToken save(WaitingToken token);

    QueueInfo.Main findByToken(String token);

    int expireExpiredTokens();

    int enqueueNewUsers(Long userCountToAddQueue);
}
