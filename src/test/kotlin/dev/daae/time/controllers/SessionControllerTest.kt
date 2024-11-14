package dev.daae.time.controllers

import dev.daae.time.models.Session
import dev.daae.time.repositories.SessionRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class SessionControllerTest(
    @Autowired
    private val sessionRepository: SessionRepository,
    @Autowired
    private val mockMvc: MockMvc
) : dev.daae.time.IntegrationTest() {

    @BeforeEach
    fun beforeEach() {
        sessionRepository!!.deleteAll()
    }

    @Test
    @Throws(Exception::class)
    fun sessionEndpointCreatesSessionInDatabase() {
        val request =
            MockMvcRequestBuilders.post("/session").with(validCredentials()).contentType(MediaType.APPLICATION_JSON)
        val result = this.mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn()
        val responseBody = result.getResponse().getContentAsString()
        Assertions.assertThat(responseBody).isEqualTo("Started.")
        val savedSession = sessionRepository!!.findFirstByOrderByStartDesc()
        Assertions.assertThat(savedSession!!.start).isNotNull()
        Assertions.assertThat(savedSession.end).isNull()
    }

    @Test
    @Throws(Exception::class)
    fun logEndpointSetsStopKindIfPreviousKindWasStart() {
        val start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC)
        sessionRepository!!.save<Session?>(Session(null, start, null))
        val request =
            MockMvcRequestBuilders.post("/session").with(validCredentials()).contentType(MediaType.APPLICATION_JSON)
        val result = this.mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn()
        val responseBody = result.getResponse().getContentAsString()
        Assertions.assertThat(responseBody).isEqualTo("Stopped.")
        val savedSession = sessionRepository.findFirstByOrderByStartDesc()
        Assertions.assertThat(savedSession!!.start).isNotNull()
        Assertions.assertThat(savedSession.end).isNotNull()
    }

    @Test
    @Throws(Exception::class)
    fun testThatAllSessionsAreDeleted() {
        val request = MockMvcRequestBuilders.delete("/session").with(validCredentials())
        val result = this.mockMvc.perform(request).andReturn()
        val responseBody = result.getResponse().getContentAsString()
        Assertions.assertThat(responseBody).isEqualTo("All sessions deleted.")
        Assertions.assertThat<Session?>(sessionRepository!!.findAll()).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun testThatSessionStartIsUpdated() {
        val start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC)
        val editedStart = LocalDateTime.of(2020, 1, 1, 0, 5).atOffset(ZoneOffset.UTC)
        val session = Session(null, start, null)
        sessionRepository!!.save<Session?>(session)
        this.mockMvc.perform(
            MockMvcRequestBuilders.put("/session/start")
                .content("{\"plus\":5}")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .with(validCredentials())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Session start updated."))
        val updatedSession = sessionRepository.findFirstByOrderByStartDesc()
        Assertions.assertThat(updatedSession!!.start).isEqualTo(editedStart)
    }

    @Test
    @Throws(Exception::class)
    fun testThatSessionEndIsNotUpdatedWhenSessionHasNotEnded() {
        val start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC)
        val session = Session(null, start, null)
        sessionRepository!!.save<Session?>(session)
        this.mockMvc.perform(
            MockMvcRequestBuilders.put("/session/end")
                .content("{\"plus\":5}")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .with(validCredentials())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Can not update session end until session has ended."))
    }
}
