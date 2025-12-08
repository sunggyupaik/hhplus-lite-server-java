package kr.hhplus.be.server.interfaces.queue;

import kr.hhplus.be.server.domain.queue.QueueService;
import kr.hhplus.be.server.support.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/queues")
public class QueueApiController {
    private final QueueService queueService;

    @PostMapping("/token")
    public CommonResponse issueToken(@RequestHeader("X-User-Token") String userToken) {
        String token = queueService.issueToken(userToken, LocalDateTime.now());
        var response = QueueDto.IssueResponse.of(token);

        return CommonResponse.success(response);
    }
}
