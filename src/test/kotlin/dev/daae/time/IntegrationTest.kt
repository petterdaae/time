package dev.daae.time

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
abstract class IntegrationTest {

    fun validCredentials(): RequestPostProcessor {
        return SecurityMockMvcRequestPostProcessors.httpBasic("username", "password")
    }

    companion object {
        @Container
        @ServiceConnection
        var postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15.6-alpine")
    }
}
