package dev.daae.time;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogRepository logRepository;

    @Test
    void statusEndpointReturns500WhenLogRepositoryThrowsException() throws Exception {
        when(logRepository.findAllByOrderByTimestampDesc()).thenThrow(new RuntimeException());
        this.mockMvc.perform(get("/status").with(httpBasic("username", "password")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }

    @Test
    void statusEndpointReturns500WhenLogRepositoryThrowsNullPointerException() throws Exception {
        when(logRepository.findAllByOrderByTimestampDesc()).thenThrow(new NullPointerException());
        this.mockMvc.perform(get("/status").with(httpBasic("username", "password")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }
}
