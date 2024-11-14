package dev.daae.time.controllers

import dev.daae.time.IntegrationTest
import dev.daae.time.models.Session
import dev.daae.time.repositories.SessionRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.lang.Exception
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

internal class StatusControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val sessionRepository: SessionRepository,
) : IntegrationTest() {

    @MockBean private var clock: Clock? = null

    @BeforeEach
    fun setup() {
        sessionRepository!!.deleteAll()
        Mockito.`when`<ZoneId?>(clock!!.getZone()).thenReturn(ZoneId.of("UTC"))
        Mockito.`when`<Instant?>(clock!!.instant()).thenReturn(Instant.now())
    }

    @Test
    @Throws(Exception::class)
    fun currentStatusEndpointReturns200WithValidCredentials() {
        val request = MockMvcRequestBuilders.get("/status/current").with(validCredentials())
        this.mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @Throws(Exception::class)
    fun currentStatusEndpointReturns401WithoutCredentials() {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/status/current"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @Throws(Exception::class)
    fun currentStatusEndpointReturns401WithInvalidCredentials() {
        val request = MockMvcRequestBuilders.get("/status/current")
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("invalid", "credentials"))
        this.mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @Throws(Exception::class)
    fun currentStatusEndpointDescribesCurrentSessionIfTheLatestLogIsStart() {
        val start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC)
        sessionRepository!!.save<Session?>(Session(null, start, null))
        Mockito.`when`<ZoneId?>(clock!!.getZone()).thenReturn(ZoneId.of("UTC"))

        mockClock(2020, 1, 1, 2, 3)
        var request = MockMvcRequestBuilders.get("/status/current").with(validCredentials())
        var result = this.mockMvc.perform(request).andReturn().getResponse().getContentAsString()
        Assertions.assertThat(result).isEqualTo("\uD83C\uDFE2 02:03")

        mockClock(2020, 1, 1, 2, 0)
        request = MockMvcRequestBuilders.get("/status/current").with(validCredentials())
        result = this.mockMvc.perform(request).andReturn().getResponse().getContentAsString()
        Assertions.assertThat(result).isEqualTo("\uD83C\uDFE2 02:00")

        mockClock(2020, 1, 1, 0, 3)
        request = MockMvcRequestBuilders.get("/status/current").with(validCredentials())
        result = this.mockMvc.perform(request).andReturn().getResponse().getContentAsString()
        Assertions.assertThat(result).isEqualTo("\uD83C\uDFE2 00:03")
    }

    @Test
    @Throws(Exception::class)
    fun currentStatusEndpointDescribesPreviousSessionIfTheLatestLogIsStop() {
        var start = LocalDateTime.of(2020, 1, 1, 0, 0).atOffset(ZoneOffset.UTC)
        var end = LocalDateTime.of(2020, 1, 1, 2, 3).atOffset(ZoneOffset.UTC)
        sessionRepository!!.save<Session?>(Session(null, start, end))

        var request = MockMvcRequestBuilders.get("/status/current").with(validCredentials())
        var result = this.mockMvc.perform(request).andReturn()
        var responseBody = result.getResponse().getContentAsString()
        Assertions.assertThat(responseBody).isEqualTo("\uD83D\uDE0C 02:03")

        start = LocalDateTime.of(2020, 1, 1, 5, 0).atOffset(ZoneOffset.UTC)
        end = LocalDateTime.of(2020, 1, 1, 8, 0).atOffset(ZoneOffset.UTC)
        sessionRepository.save<Session?>(Session(null, start, end))

        request = MockMvcRequestBuilders.get("/status/current").with(validCredentials())
        result = this.mockMvc.perform(request).andReturn()
        responseBody = result.getResponse().getContentAsString()
        Assertions.assertThat(responseBody).isEqualTo("\uD83D\uDE0C 03:00")
    }

    @Test
    @Throws(Exception::class)
    fun currentStatusEndpointReturnsEmptyMessageWhenThereAreNoSessionsInTheDatabase() {
        val request = MockMvcRequestBuilders.get("/status/current").with(validCredentials())
        val result = this.mockMvc.perform(request).andReturn()
        val responseBody = result.getResponse().getContentAsString()
        Assertions.assertThat(responseBody).isEqualTo("No sessions in database.")
    }

    @Test
    @Throws(Exception::class)
    fun testWeekStatus() {
        mockClock(2024, 5, 22, 20, 15)

        var start = LocalDateTime.of(2024, 5, 20, 8, 0).atOffset(ZoneOffset.UTC)
        var end = LocalDateTime.of(2024, 5, 20, 16, 0).atOffset(ZoneOffset.UTC)
        val monday = Session(null, start, end)
        sessionRepository!!.save<Session?>(monday)

        start = LocalDateTime.of(2024, 5, 21, 8, 0).atOffset(ZoneOffset.UTC)
        end = LocalDateTime.of(2024, 5, 21, 16, 3).atOffset(ZoneOffset.UTC)
        val tuesday = Session(null, start, end)
        sessionRepository.save<Session?>(tuesday)

        val request = MockMvcRequestBuilders.get("/status/week").with(validCredentials())
        this.mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("✅ 16:03\n⌛ 23:57"))
    }

    private fun mockClock(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val time = LocalDateTime.of(year, month, day, hour, minute).atOffset(ZoneOffset.UTC)
        Mockito.`when`<Instant?>(clock!!.instant()).thenReturn(time.toInstant())
    }
}
