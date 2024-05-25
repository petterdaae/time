package dev.daae.time.services;

import dev.daae.time.repository.SessionRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusService {

  private final SessionRepository sessionRepository;

  private final Clock clock;

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
      return "In progress, " + formattedDuration + ".";
    }

    return "Previous, " + formattedDuration + ".";
  }

  private String formatDuration(Duration duration) {
    var formattedDuration = "";

    if (duration.toHours() != 0) {
      formattedDuration += duration.toHours() + " hours";
    }

    if (duration.toMinutesPart() != 0) {
      if (!formattedDuration.isEmpty()) {
        formattedDuration += " and ";
      }

      formattedDuration += duration.toMinutesPart() + " minutes";
    }

    if (formattedDuration.isEmpty()) {
      return "0 minutes";
    }

    return formattedDuration;
  }
}
