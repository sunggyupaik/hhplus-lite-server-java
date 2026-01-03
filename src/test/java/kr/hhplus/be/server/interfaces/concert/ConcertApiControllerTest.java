package kr.hhplus.be.server.interfaces.concert;

import kr.hhplus.be.server.config.interceptor.WebConfig;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.queue.QueueInfo;
import kr.hhplus.be.server.domain.queue.QueueService;
import kr.hhplus.be.server.domain.queue.WaitingToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertApiController.class)
@Import(WebConfig.class)
public class ConcertApiControllerTest {
    private static final String X_WAITING_TOKEN = "X-Waiting-Token";
    private static final String EXISTED_TOKEN = "tokenCreate";
    private static final String NOT_EXISTED_TOKEN = "token-123";

    @Autowired MockMvc mockMvc;
    @MockitoBean ConcertService concertService;
    @MockitoBean QueueService queueService;

    @BeforeEach
    void setUp() {
        given(queueService.getStatus(EXISTED_TOKEN)).willReturn(waitingTokenResponse());
    }

    @Test
    void 대기열_토큰이_없으면_토큰이_없다는_예외를_던진다() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/concerts/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("대기열 토큰을 발급받아야 합니다"));
    }

    @Test
    void 대기열_토큰이_존재하지않으면_잘못요청했다는_예외를_던진다() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/concerts/schedules")
                        .header(X_WAITING_TOKEN, NOT_EXISTED_TOKEN)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("유효하지 않은 토큰입니다"));
    }

    @Test
    void 대기열_토큰이_존재하면_토큰검사를_통과한다() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/concerts/schedules")
                                .header(X_WAITING_TOKEN, EXISTED_TOKEN)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .doesNotExist());
    }

    private QueueInfo.Main waitingTokenResponse() {
        return QueueInfo.Main.builder()
                .waitingTokenId(100L)
                .userId(1L)
                .waitingNumber(5)
                .status(WaitingToken.Status.HELD)
                .build();
    }
}
