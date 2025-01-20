package dev.daae.time.repositories

import dev.daae.time.models.Session
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class SessionRepositoryTest(
    @Autowired
    private var repository: SessionRepository,
) : dev.daae.time.IntegrationTest() {
    @Test
    fun testThatLogIsReturnedFromRepositoryAfterItIsSaved() {
        val now = LocalDateTime.now().atOffset(ZoneOffset.UTC)
        val session = repository.save<Session>(Session(null, now, now))
        session.id?.let { repository.findById(it).orElseThrow() }
    }

    @Test
    @Disabled // TODO: Fix timezone issue
    fun testThatTimestampIsEqualBeforeAndAfterStoringIt() {
        val timestamp = LocalDateTime.now().atOffset(ZoneOffset.UTC)
        val id = repository.save(Session(null, timestamp, null)).id
        val session: Session = id?.let { repository.findById(it).orElseThrow() }!!
        Assertions.assertThat(session.start).isEqualTo(timestamp)
    }
}
