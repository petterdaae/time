package dev.daae.time.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.daae.time.IntegrationTest;
import dev.daae.time.models.Session;
import dev.daae.time.repositories.SessionRepository;
import java.time.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class StatusControllerTest extends IntegrationTest {

    @Autowired
    private SessionRepository sessionRepository;

    @MockBean
    private Clock clock;

    @BeforeEach
    public void setup() {
        sessionRepository.deleteAll();
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        when(clock.instant()).thenReturn(Instant.now());
    }

    @Test
    void currentStatusEndpointReturns200WithValidCredentials() throws Exception {
        var request = get("/status/current").with(validCredentials());
        this.mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void currentStatusEndpointReturns401WithoutCredentials() throws Exception {
        this.mockMvc.perform(get("/status/current")).andExpect(status().isUnauthorized());
    }

    @Test
    void currentStatusEndpointReturns401WithInvalidCredentials() throws Exception {
        var request = get("/status/current").with(httpBasic("invalid", "credentials"));
        this.mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    void currentStatusEndpointDescribesCurrentSessionIfTheLatestLogIsStart() throws Exception {
        var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
        sessionRepository.save(new Session(null, start, null));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        mockClock(2020, 1, 1, 2, 3);
        var request = get("/status/current").with(validCredentials());
        var result = this.mockMvc.perform(request).andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo("\uD83C\uDFE2 02:03");

        mockClock(2020, 1, 1, 2, 0);
        request = get("/status/current").with(validCredentials());
        result = this.mockMvc.perform(request).andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo("\uD83C\uDFE2 02:00");

        mockClock(2020, 1, 1, 0, 3);
        request = get("/status/current").with(validCredentials());
        result = this.mockMvc.perform(request).andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo("\uD83C\uDFE2 00:03");
    }

    @Test
    void currentStatusEndpointDescribesPreviousSessionIfTheLatestLogIsStop() throws Exception {
        var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
        var end = LocalDateTime.of(2020, 1, 1, 2, 3).atOffset(ZoneOffset.UTC);
        sessionRepository.save(new Session(null, start, end));

        var request = get("/status/current").with(validCredentials());
        var result = this.mockMvc.perform(request).andReturn();
        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("\uD83D\uDE0C 02:03");

        start = LocalDateTime.of(2020, 1, 1, 5, 0).atOffset(ZoneOffset.UTC);
        end = LocalDateTime.of(2020, 1, 1, 8, 0).atOffset(ZoneOffset.UTC);
        sessionRepository.save(new Session(null, start, end));

        request = get("/status/current").with(validCredentials());
        result = this.mockMvc.perform(request).andReturn();
        responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("\uD83D\uDE0C 03:00");
    }

    @Test
    void currentStatusEndpointReturnsEmptyMessageWhenThereAreNoSessionsInTheDatabase() throws Exception {
        var request = get("/status/current").with(validCredentials());
        var result = this.mockMvc.perform(request).andReturn();
        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("No sessions in database.");
    }

    @Test
    void testWeekStatus() throws Exception {
        mockClock(2024, 5, 22, 20, 15);

        var start = LocalDateTime.of(2024, 5, 20, 8, 0).atOffset(ZoneOffset.UTC);
        var end = LocalDateTime.of(2024, 5, 20, 16, 0).atOffset(ZoneOffset.UTC);
        var monday = new Session(null, start, end);
        sessionRepository.save(monday);

        start = LocalDateTime.of(2024, 5, 21, 8, 0).atOffset(ZoneOffset.UTC);
        end = LocalDateTime.of(2024, 5, 21, 16, 3).atOffset(ZoneOffset.UTC);
        var tuesday = new Session(null, start, end);
        sessionRepository.save(tuesday);

        var request = get("/status/week").with(validCredentials());
        this.mockMvc.perform(request).andExpect(status().isOk()).andExpect(content().string("✅ 16:03\n⌛ 23:57"));
    }

    private void mockClock(int year, int month, int day, int hour, int minute) {
        var time = LocalDateTime.of(year, month, day, hour, minute).atOffset(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(time.toInstant());
    }
}
