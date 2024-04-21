package dev.daae.time;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.daae.time.models.CreateLogResponse;
import dev.daae.time.models.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTests {

    private static final Logger log = LoggerFactory.getLogger(ControllerTests.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogRepository logRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        logRepository.deleteAll();
    }

    @Test
    void statusEndpointReturns200WithValidCredentials() throws Exception {
        this.mockMvc.perform(get("/status").with(httpBasic("username", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void statusEndpointReturns401WithoutCredentials() throws Exception {
        this.mockMvc.perform(get("/status")).andExpect(status().isUnauthorized());
    }

    @Test
    void statusEndpointReturns401WithInvalidCredentials() throws Exception {
        this.mockMvc.perform(get("/status").with(httpBasic("invalid", "credentials")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void statusEndpointReturnsCorrectPreviousDifference() throws Exception {
        var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
        var stop = LocalDateTime.of(2020, 1, 1, 1, 0).atOffset(ZoneOffset.UTC);
        logRepository.save(Log.builder().kind(Log.Kind.START).timestamp(start).build());
        logRepository.save(Log.builder().kind(Log.Kind.STOP).timestamp(stop).build());
        this.mockMvc.perform(get("/status").with(httpBasic("username", "password")))
                .andExpect(jsonPath("$.status").value("Stopped"))
                .andExpect(jsonPath("$.stats.previous").value("01:00:00"));
    }

    @Test
    void logEndpointCreatesLogInDatabase() throws Exception {
        var result = this.mockMvc.perform(
                post("/log")
                        .with(csrf())
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        var response = mapper.readValue(result.getResponse().getContentAsString(), CreateLogResponse.class);
        var savedLog = logRepository.findById(response.id()).orElseThrow();
        assertThat(savedLog.getKind()).isEqualTo(Log.Kind.START);
    }

    @Test
    void logEndpointSetsStopKindIfPreviousKindWasStart() throws Exception {
        var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
        logRepository.save(Log.builder().kind(Log.Kind.START).timestamp(start).build());
        var result = this.mockMvc.perform(
                post("/log")
                        .with(csrf())
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        var response = mapper.readValue(result.getResponse().getContentAsString(), CreateLogResponse.class);
        var savedLog = logRepository.findById(response.id()).orElseThrow();
        assertThat(savedLog.getKind()).isEqualTo(Log.Kind.STOP);
    }
}
