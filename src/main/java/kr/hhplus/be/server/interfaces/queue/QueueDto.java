package kr.hhplus.be.server.interfaces.queue;

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
}
