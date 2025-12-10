package kr.hhplus.be.server.domain.queue;

import lombok.Builder;

public class QueueInfo {
    @Builder
    public record Main(
            Long waitingTokenId,
            Long userId,
            Integer waitingNumber,
            WaitingToken.Status status
    ) {
        public static Main of(MainIF mainIF) {
            return Main.builder()
                    .waitingTokenId(mainIF.getWaitingTokenId())
                    .userId(mainIF.getUserId())
                    .waitingNumber(mainIF.getWaitingNumber())
                    .status(mainIF.getStatus())
                    .build();
        }
    }

    @Builder
    public record QueueRefreshCountInfo(
            int updateToExpiredTokenCount,
            int updateToHeldTokenCount
    ) {

        public static QueueRefreshCountInfo of(int updateToExpiredTokenCount, int updateToHeldTokenCount) {
            return QueueRefreshCountInfo.builder()
                    .updateToExpiredTokenCount(updateToExpiredTokenCount)
                    .updateToHeldTokenCount(updateToHeldTokenCount)
                    .build();
        }
    }

    public interface MainIF {
        Long getWaitingTokenId();
        Long getUserId();
        Integer getWaitingNumber();
        WaitingToken.Status getStatus();
    }
}
