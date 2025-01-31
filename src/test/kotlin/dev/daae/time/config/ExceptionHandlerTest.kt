package dev.daae.time.config

import dev.daae.time.IntegrationTest
import dev.daae.time.models.Session
import dev.daae.time.repositories.SessionRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.RuntimeException

@AutoConfigureMockMvc
internal class ExceptionHandlerTest() : IntegrationTest() {
    @Autowired
    private val mockMvc: MockMvc? = null

    @MockitoBean
    private val sessionRepository: SessionRepository? = null

    @Test
    @Throws(Exception::class)
    fun statusEndpointReturns500WhenLogRepositoryThrowsException() {
        Mockito.`when`<Session?>(sessionRepository!!.findFirstByOrderByStartDesc()).thenThrow(RuntimeException())
        this.mockMvc!!.perform(
            MockMvcRequestBuilders.get("/status")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("username", "password")),
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Internal server error"))
    }

    @Test
    @Throws(Exception::class)
    fun statusEndpointReturns500WhenLogRepositoryThrowsNullPointerException() {
        Mockito.`when`<Session?>(sessionRepository!!.findFirstByOrderByStartDesc()).thenThrow(NullPointerException())
        this.mockMvc!!.perform(
            MockMvcRequestBuilders.get("/status")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("username", "password")),
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Internal server error"))
    }
}
