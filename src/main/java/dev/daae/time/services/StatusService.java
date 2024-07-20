package dev.daae.time.services;

import dev.daae.time.models.Session;
import dev.daae.time.repositories.SessionRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final SessionRepository sessionRepository;

    private final Clock clock;

    private static final Duration ONE_WEEK_DURATION = Duration.ofHours(37).plus(Duration.ofMinutes(30));

    public String currentStatus() {
        var optionalLatest = sessionRepository.findFirstByOrderByStartDesc();
        if (optionalLatest.isEmpty()) {
            return "No sessions in database.";
        }
        var latest = optionalLatest.get();

        var now = LocalDateTime.now(clock).atOffset(ZoneOffset.UTC);
        var start = latest.getStart();
        var end = latest.getEnd().orElse(now);

        var duration = Duration.between(start, end);
        var formattedDuration = formatDuration(duration);

        if (latest.getEnd().isEmpty()) {
            return "\uD83C\uDFE2 " + formattedDuration;
        }

        return "\uD83D\uDE0C " + formattedDuration;
    }

    public String todayStatus() {
        var now = OffsetDateTime.now(clock);
        var sessionsToday = sessionRepository.findSessionsToday(now);
        var duration = durationOfSessions(sessionsToday);
        var formattedDuration = formatDuration(duration);
        return formattedDuration;
    }

    public String weekStatus() {
        var now = OffsetDateTime.now(clock);
        var sessionsThisWeek = sessionRepository.findSessionsThisWeek(now);

        var durationThisWeek = durationOfSessions(sessionsThisWeek);
        var remainingDuration = ONE_WEEK_DURATION.minus(durationThisWeek);

        var formattedDurationThisWeek = formatDuration(durationThisWeek);
        var formattedRemainingDuration = formatDuration(remainingDuration);

        return String.format("✅ %s\n⌛ %s", formattedDurationThisWeek, formattedRemainingDuration);
    }

    private Duration durationOfSessions(List<Session> sessions) {
        var completedDuration = sessions
            .stream()
            .filter(session -> session.getEnd().isPresent())
            .map(session -> Duration.between(session.getStart(), session.getEnd().get()))
            .reduce(Duration.ZERO, Duration::plus);

        var now = OffsetDateTime.now(clock);
        var inProgressDuration = sessions
            .stream()
            .filter(session -> session.getEnd().isEmpty())
            .map(session -> Duration.between(session.getStart(), now))
            .reduce(Duration.ZERO, Duration::plus);

        return completedDuration.plus(inProgressDuration);
    }

    private String formatDuration(Duration duration) {
        var hours = StringUtils.leftPad(Long.toString(duration.toHours()), 2, '0');
        var minutes = StringUtils.leftPad(Integer.toString(duration.toMinutesPart()), 2, '0');
        return hours + ":" + minutes;
    }
}
