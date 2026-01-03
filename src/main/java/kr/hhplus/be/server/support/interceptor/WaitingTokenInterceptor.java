package kr.hhplus.be.server.support.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.queue.QueueInfo;
import kr.hhplus.be.server.domain.queue.QueueService;
import kr.hhplus.be.server.domain.queue.WaitingToken;
import kr.hhplus.be.server.support.exception.IllegalStatusException;
import kr.hhplus.be.server.support.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class WaitingTokenInterceptor implements HandlerInterceptor {
    private static final String X_WAITING_TOKEN = "X-Waiting-Token";

    private final QueueService queueService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        // Controller 메서드가 아닌 경우 패스 (정적 리소스 등)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String waitingToken = request.getHeader(X_WAITING_TOKEN);

        if (waitingToken == null || waitingToken.isBlank()) {
            throw new InvalidParamException("대기열 토큰을 발급받아야 합니다");
        }

        QueueInfo.Main findToken = queueService.getStatus(waitingToken);
        if (findToken == null) {
            throw new InvalidParamException("유효하지 않은 토큰입니다");
        }

        if (findToken.status() != WaitingToken.Status.HELD) {
            throw new IllegalStatusException("대기열 토큰이 아직 대기중입니다");
        }

        return true;
    }
}
