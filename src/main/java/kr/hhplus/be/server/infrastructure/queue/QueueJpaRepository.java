package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.QueueInfo;
import kr.hhplus.be.server.domain.queue.WaitingToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QueueJpaRepository extends JpaRepository<WaitingToken, Long> {
    @Query("""
    SELECT 
        wt.id AS waitingTokenId,
        wt.userId AS userId,
        wt.status AS status,
        (
            SELECT COUNT(t2) + 1
            FROM WaitingToken t2
            WHERE t2.status <> 'EXPIRED'
              AND t2.expiredAt > CURRENT_TIMESTAMP
              AND t2.createdAt < wt.createdAt
        ) AS waitingNumber
    FROM WaitingToken wt
    WHERE wt.token = :token
    """)
    QueueInfo.MainIF findByWaitingToken(@Param("token") String waitingToken);
}
