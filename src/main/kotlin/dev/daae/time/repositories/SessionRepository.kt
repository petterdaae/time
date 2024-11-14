package dev.daae.time.repositories

import dev.daae.time.models.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.OffsetDateTime
import java.util.Optional

interface SessionRepository : JpaRepository<Session?, Long?> {
    fun findFirstByOrderByStartDesc(): Session?

    @Query(
        value = "select * from session where start >= date_trunc('week', cast(? as timestamptz))",
        nativeQuery = true
    )
    fun findSessionsThisWeek(now: OffsetDateTime?): MutableList<Session?>?

    @Query(value = "select * from session where start >= date_trunc('day', cast(? as timestamptz))", nativeQuery = true)
    fun findSessionsToday(now: OffsetDateTime?): MutableList<Session?>?
}
