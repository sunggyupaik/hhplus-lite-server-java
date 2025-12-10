package kr.hhplus.be.server.interfaces.queue;

import kr.hhplus.be.server.domain.queue.QueueInfo;
import kr.hhplus.be.server.domain.queue.WaitingToken;
import lombok.Builder;

public class QueueDto {
    @Builder
    public record IssueResponse(
            String token
    ) {
        public static IssueResponse of(String token) {
            return IssueResponse.builder()
                    .token(token)
                    .build();
        }
    }

    @Builder
    public record Main(
            Long waitingTokenId,
            Long userId,
            Integer waitingNumber,
            WaitingToken.Status status
    ) {
        public static Main of(QueueInfo.Main main) {
            return Main.builder()
                    .waitingTokenId(main.waitingTokenId())
                    .userId(main.userId())
                    .waitingNumber(main.waitingNumber())
                    .status(main.status())
                    .build();
        }
    }
}
