package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.QueueInfo;
import kr.hhplus.be.server.domain.queue.WaitingToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface QueueJpaRepository extends JpaRepository<WaitingToken, Long> {
    @Query("""
    SELECT 
        wt.id AS waitingTokenId,
        wt.userId AS userId,
        wt.status AS status,
        (
            SELECT COUNT(t2) + 1
            FROM WaitingToken t2
            WHERE t2.status = 'WAITING'
              AND t2.expiredAt > CURRENT_TIMESTAMP
              AND t2.createdAt < wt.createdAt
        ) AS waitingNumber
    FROM WaitingToken wt
    WHERE wt.token = :token
    """)
    QueueInfo.MainIF findByWaitingToken(@Param("token") String waitingToken);

    @Modifying
    @Transactional
    @Query("""
        UPDATE WaitingToken wt
        SET wt.status = 'EXPIRED'
        WHERE CURRENT_TIMESTAMP > wt.expiredAt
          AND wt.status <> 'EXPIRED'  
    """)
    int expireExpiredTokens();

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE waiting_tokens wt
        JOIN (
            SELECT waiting_token_id
            FROM waiting_tokens
            WHERE status = 'WAITING'
            ORDER BY created_at
            LIMIT :userCountToAddQueue
        ) AS tmp ON wt.waiting_token_id = tmp.waiting_token_id
        SET wt.status = 'HELD'
    """, nativeQuery = true)
    int enqueueNewUsers(@Param("userCountToAddQueue") Long userCountToAddQueue);
}
