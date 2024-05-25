package dev.daae.time;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.daae.time.models.Session;
import dev.daae.time.repository.SessionRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc
public class ControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private SessionRepository sessionRepository;

  @MockBean private Clock clock;

  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  public void setup() {
    sessionRepository.deleteAll();
    when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    when(clock.instant()).thenReturn(Instant.now());
  }

  @Test
  void currentStatusEndpointReturns200WithValidCredentials() throws Exception {
    this.mockMvc
        .perform(get("/status/current").with(httpBasic("username", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void currentStatusEndpointReturns401WithoutCredentials() throws Exception {
    this.mockMvc.perform(get("/status/current")).andExpect(status().isUnauthorized());
  }

  @Test
  void currentStatusEndpointReturns401WithInvalidCredentials() throws Exception {
    this.mockMvc
        .perform(get("/status/current").with(httpBasic("invalid", "credentials")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void sessionEndpointCreatesSessionInDatabase() throws Exception {
    var result =
        this.mockMvc
            .perform(
                post("/session")
                    .with(httpBasic("username", "password"))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
    var responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("Started.");
    var savedSession = sessionRepository.findFirstByOrderByStartDesc().orElseThrow();
    assertThat(savedSession.getStart()).isNotNull();
    assertThat(savedSession.getEnd()).isEmpty();
  }

  @Test
  void logEndpointSetsStopKindIfPreviousKindWasStart() throws Exception {
    var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
    sessionRepository.save(Session.builder().start(start).build());
    var result =
        this.mockMvc
            .perform(
                post("/session")
                    .with(httpBasic("username", "password"))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
    var responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("Stopped.");
    var savedSession = sessionRepository.findFirstByOrderByStartDesc().orElseThrow();
    assertThat(savedSession.getStart()).isNotNull();
    assertThat(savedSession.getEnd()).isPresent();
  }

  @Test
  void logEndpointReturnsCorrectAmountOfLogs() throws Exception {
    var sessionBuilder = Session.builder().start(OffsetDateTime.now());
    for (int i = 0; i < 5; i++) {
      sessionRepository.save(sessionBuilder.build());
    }
    this.mockMvc
        .perform(get("/session").with(csrf()).with(httpBasic("username", "password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(5));
  }

  @Test
  void currentStatusEndpointDescribesCurrentSessionIfTheLatestLogIsStart() throws Exception {
    var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
    sessionRepository.save(Session.builder().start(start).build());
    when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

    var end = LocalDateTime.of(2020, 1, 1, 2, 3).atOffset(ZoneOffset.UTC);
    when(clock.instant()).thenReturn(end.toInstant());
    var result =
        this.mockMvc
            .perform(get("/status/current").with(httpBasic("username", "password")))
            .andReturn();
    var responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("In progress, 2 hours and 3 minutes.");

    end = LocalDateTime.of(2020, 1, 1, 2, 0).atOffset(ZoneOffset.UTC);
    when(clock.instant()).thenReturn(end.toInstant());
    result =
        this.mockMvc
            .perform(get("/status/current").with(httpBasic("username", "password")))
            .andReturn();
    responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("In progress, 2 hours.");

    end = LocalDateTime.of(2020, 1, 1, 0, 3).atOffset(ZoneOffset.UTC);
    when(clock.instant()).thenReturn(end.toInstant());
    result =
        this.mockMvc
            .perform(get("/status/current").with(httpBasic("username", "password")))
            .andReturn();
    responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("In progress, 3 minutes.");
  }

  @Test
  void currentStatusEndpointDescribesPreviousSessionIfTheLatestLogIsStop() throws Exception {
    var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
    var end = LocalDateTime.of(2020, 1, 1, 2, 3).atOffset(ZoneOffset.UTC);
    sessionRepository.save(Session.builder().start(start).end(end).build());
    var result =
        this.mockMvc
            .perform(get("/status/current").with(httpBasic("username", "password")))
            .andReturn();
    var responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("Previous, 2 hours and 3 minutes.");

    start = LocalDateTime.of(2020, 1, 1, 5, 0).atOffset(ZoneOffset.UTC);
    end = LocalDateTime.of(2020, 1, 1, 8, 0).atOffset(ZoneOffset.UTC);
    sessionRepository.save(Session.builder().start(start).end(end).build());
    result =
        this.mockMvc
            .perform(get("/status/current").with(httpBasic("username", "password")))
            .andReturn();
    responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("Previous, 3 hours.");
  }

  @Test
  void currentStatusEndpointReturnsEmptyMessageWhenThereAreNoSessionsInTheDatabase() throws Exception {
    var result =
        this.mockMvc
            .perform(get("/status/current").with(httpBasic("username", "password")))
            .andReturn();
    var responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("No sessions in database.");
  }

  @Test
  void testThatAllSessionsAreDeleted() throws Exception {
    var result =
        this.mockMvc.perform(delete("/session").with(httpBasic("username", "password"))).andReturn();
    var responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isEqualTo("All sessions deleted.");
    assertThat(sessionRepository.findAll()).isEmpty();
  }
}
