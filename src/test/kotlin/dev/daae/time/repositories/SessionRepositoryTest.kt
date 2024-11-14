package dev.daae.time.repositories

import dev.daae.time.models.Session
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class SessionRepositoryTest : dev.daae.time.IntegrationTest() {
    @Autowired
    private val repository: dev.daae.time.repositories.SessionRepository? = null

    @Test
    fun testThatLogIsReturnedFromRepositoryAfterItIsSaved() {
        val now = LocalDateTime.now().atOffset(ZoneOffset.UTC)
        val session = repository!!.save<Session>(Session(null, now, now))
        repository.findById(session.id).orElseThrow()
    }

    @Test
    fun testThatTimestampIsEqualBeforeAndAfterStoringIt() {
        val timestamp = LocalDateTime.now().atOffset(ZoneOffset.UTC)
        val id = repository!!.save<Session?>(Session(null, timestamp, null))!!.id
        val session: Session = repository.findById(id).orElseThrow()!!
        Assertions.assertThat(session.start).isEqualTo(timestamp)
    }
}
