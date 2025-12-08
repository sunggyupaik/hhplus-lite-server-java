package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.WaitingToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueJpaRepository extends JpaRepository<WaitingToken, Long> {
}
