package com.example.concert.interceptor;

import com.example.concert.common.CustomException;
import com.example.concert.interceptor.fixtures.WaitingQueueFixtureFactory;
import com.example.concert.utils.TimeProvider;
import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.repository.WaitingQueueRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@WebMvcTest(TokenValidationInterceptor.class)
public class TokenValidationInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeProvider timeProvider;
    @MockBean
    private WaitingQueueRepository waitingQueueRepository;
    @Autowired
    private TokenValidationInterceptor tokenValidationInterceptor;
    @BeforeEach
    public void setUp(){
        WaitingQueue validQueue = WaitingQueueFixtureFactory.createWaitingQueueWithTokenCreated5MinutesAgo("valid-token");
        WaitingQueue invalidQueue = WaitingQueueFixtureFactory.createWaitingQueueWithTokenCreated15MinutesAgo("invalid-token");
        WaitingQueue waitingQueue = WaitingQueueFixtureFactory.createWaitingQueueWithWaitingToken("waiting-token");

        given(waitingQueueRepository.findByToken("valid-token")).willReturn(Optional.of(validQueue));
        given(waitingQueueRepository.findByToken("invalid-token")).willReturn(Optional.of(invalidQueue));
        given(waitingQueueRepository.findByToken("waiting-token")).willReturn(Optional.of(waitingQueue));
        given(timeProvider.now()).willReturn(LocalDateTime.now());
    }

    @Nested
    @DisplayName("토큰을 포함한 요청을 할 때")
    class 토큰을_포함한_요청을_할때 {

        @Test
        @DisplayName("현재 기준 5분전에 생성된 ACTIVE 토큰으로 요청을 하는 경우, 토큰 검증을 통과한다.")
        void 현재_기준_5분전에_생성된_ACTIVE_토큰으로_요청을_하는_경우_토큰_검증을_통과한다 () throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.AUTHORIZATION, "valid-token");
            MockHttpServletResponse response = new MockHttpServletResponse();

            tokenValidationInterceptor.preHandle(request, response, new Object());
        }

        @Test
        @DisplayName("현재 기준 15분전에 생성된 ACTIVE 토큰으로 요청을 하는 경우, 토큰 검증이 실패한다.")
        void 현재_기준_15분전에_생성된_ACTIVE_토큰으로_요청을_하는_경우_토큰_검증이_실패한다 () {

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.AUTHORIZATION, "invalid-token");
            MockHttpServletResponse response = new MockHttpServletResponse();

            Assertions.assertThrows(CustomException.class, () -> {
                tokenValidationInterceptor.preHandle(request, response, new Object());
            });
        }

        @Test
        @DisplayName("현재 기준 5분전에 생성된 WAITING 토큰으로 요청을 하는 경우, 토큰 검증에 실패한다.")
        void 현재_기준_5분전에_생성된_WAITING_토큰으로_요청을_하는_경우_토큰_검증에_실패한다 () {

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.AUTHORIZATION, "waiting-token");
            MockHttpServletResponse response = new MockHttpServletResponse();

            Assertions.assertThrows(CustomException.class, () -> {
                tokenValidationInterceptor.preHandle(request, response, new Object());
            });
        }
    }
}
