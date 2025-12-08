package kr.hhplus.be.server.domain.queue;

import java.time.LocalDateTime;

public interface QueueService {
    String issueToken(String userToken, LocalDateTime createdAt);
}
