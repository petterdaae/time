package dev.daae.time.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.daae.time.IntegrationTest;
import dev.daae.time.models.Session;
import dev.daae.time.repositories.SessionRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class SessionControllerTest extends IntegrationTest {

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void beforeEach() {
        sessionRepository.deleteAll();
    }

    @Test
    void sessionEndpointCreatesSessionInDatabase() throws Exception {
        var result =
            this.mockMvc.perform(
                    post("/session").with(httpBasic("username", "password")).contentType(MediaType.APPLICATION_JSON)
                )
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
            this.mockMvc.perform(
                    post("/session").with(httpBasic("username", "password")).contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Stopped.");
        var savedSession = sessionRepository.findFirstByOrderByStartDesc().orElseThrow();
        assertThat(savedSession.getStart()).isNotNull();
        assertThat(savedSession.getEnd()).isPresent();
    }

    @Test
    void testThatAllSessionsAreDeleted() throws Exception {
        var result = this.mockMvc.perform(delete("/session").with(httpBasic("username", "password"))).andReturn();
        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("All sessions deleted.");
        assertThat(sessionRepository.findAll()).isEmpty();
    }

    @Test
    void testThatSessionStartIsUpdated() throws Exception {
        var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
        var editedStart = LocalDateTime.of(2020, 1, 1, 0, 5).atOffset(ZoneOffset.UTC);
        var session = Session.builder().start(start).build();
        sessionRepository.save(session);
        this.mockMvc.perform(
                put("/session/start")
                    .content("{\"plus\":5}")
                    .header("Content-Type", "application/json")
                    .with(httpBasic("username", "password"))
            )
            .andExpect(status().isOk())
            .andExpect(content().string("Session start updated."));
        var updatedSession = sessionRepository.findFirstByOrderByStartDesc().orElseThrow();
        assertThat(updatedSession.getStart()).isEqualTo(editedStart);
    }

    @Test
    void testThatSessionEndIsNotUpdatedWhenSessionHasNotEnded() throws Exception {
        var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC);
        var session = Session.builder().start(start).build();
        sessionRepository.save(session);
        this.mockMvc.perform(
                put("/session/end")
                    .content("{\"plus\":5}")
                    .header("Content-Type", "application/json")
                    .with(httpBasic("username", "password"))
            )
            .andExpect(status().isOk())
            .andExpect(content().string("Can not update session end until session has ended."));
    }
}
