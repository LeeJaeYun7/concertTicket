package com.example.concert.filter;

import com.example.concert.common.CustomException;
import com.example.concert.filter.fixtures.WaitingQueueFixtureFactory;
import com.example.concert.utils.TimeProvider;
import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.repository.WaitingQueueRepository;
import jakarta.servlet.FilterChain;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@WebMvcTest(TokenValidationFilter.class)
public class TokenValidationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimeProvider timeProvider;
    @MockBean
    private WaitingQueueRepository waitingQueueRepository;
    @Autowired
    private TokenValidationFilter tokenValidationFilter;
    @BeforeEach
    public void setUp(){

        WaitingQueue validQueue = WaitingQueueFixtureFactory.createWaitingQueueWithToken("valid-token");

        given(waitingQueueRepository.findByToken("valid-token")).willReturn(Optional.of(validQueue));
        given(waitingQueueRepository.findByToken("invalid-token")).willReturn(Optional.empty());
        given(timeProvider.now()).willReturn(LocalDateTime.now());
    }

    @Nested
    @DisplayName("토큰을 포함한 요청을 할 때")
    class 토큰을_포함한_요청을_할때 {

        @Test
        @DisplayName("유효한 토큰으로 요청을 하는 경우, 토큰 검증을 통과한다.")
        void 유효한_토큰으로_요청을_하는_경우_토큰_검증을_통과한다 () throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.AUTHORIZATION, "valid-token");
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);

            tokenValidationFilter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 요청을 하는 경우, 토큰 검증이 실패한다.")
        void 유효하지_않은_토큰으로_요청을_하는_경우_토큰_검증이_실패한다 () {

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.AUTHORIZATION, "invalid-token");
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);

            Assertions.assertThrows(CustomException.class, () -> {
                tokenValidationFilter.doFilter(request, response, chain);
            });
        }
    }
}
