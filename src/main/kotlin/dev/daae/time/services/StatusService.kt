package dev.daae.time.services

import dev.daae.time.models.Session
import dev.daae.time.repositories.SessionRepository
import org.flywaydb.core.internal.util.StringUtils
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class StatusService(
    private val sessionRepository: SessionRepository,
    private val clock: Clock,
) {

    fun currentStatus(): String {
        val latest = sessionRepository.findFirstByOrderByStartDesc() ?: return "No sessions in database."

        val now = LocalDateTime.now(clock).atOffset(ZoneOffset.UTC)
        val start = latest.start
        val end = if (latest.end != null) latest.end else now

        val duration = Duration.between(start, end)
        val formattedDuration = formatDuration(duration)

        if (latest.end == null) {
            return "\uD83C\uDFE2 $formattedDuration"
        }

        return "\uD83D\uDE0C $formattedDuration"
    }

    fun todayStatus(): String {
        val now = OffsetDateTime.now(clock)
        val sessionsToday = sessionRepository.findSessionsToday(now)
        val duration = durationOfSessions(sessionsToday!!)
        val formattedDuration = formatDuration(duration)
        return formattedDuration
    }

    fun weekStatus(): String {
        val now = OffsetDateTime.now(clock)
        val sessionsThisWeek = sessionRepository.findSessionsThisWeek(now)

        val durationThisWeek = durationOfSessions(sessionsThisWeek!!)
        val remainingDuration = ONE_WEEK_DURATION.minus(durationThisWeek)

        val formattedDurationThisWeek = formatDuration(durationThisWeek)
        val formattedRemainingDuration = formatDuration(remainingDuration)

        return String.format("✅ %s\n⌛ %s", formattedDurationThisWeek, formattedRemainingDuration)
    }

    private fun durationOfSessions(sessions: MutableList<Session?>): Duration {
        val completedDuration =
            sessions
                .stream()
                .filter { session: Session? -> session!!.end != null }
                .map { session: Session? -> Duration.between(session!!.start, session.end) }
                .reduce(Duration.ZERO) { obj: Duration?, duration: Duration? -> obj!!.plus(duration) }

        val now = OffsetDateTime.now(clock)
        val inProgressDuration =
            sessions
                .stream()
                .filter { session: Session? -> session!!.end == null }
                .map { session: Session? -> Duration.between(session!!.start, now) }
                .reduce(Duration.ZERO) { obj: Duration?, duration: Duration? -> obj!!.plus(duration) }

        return completedDuration.plus(inProgressDuration)
    }

    private fun formatDuration(duration: Duration): String {
        val hours = StringUtils.leftPad(duration.toHours().toString(), 2, '0')
        val minutes = StringUtils.leftPad(duration.toMinutesPart().toString(), 2, '0')
        return "$hours:$minutes"
    }

    companion object {
        private val ONE_WEEK_DURATION: Duration = Duration.ofHours(40)
    }
}
