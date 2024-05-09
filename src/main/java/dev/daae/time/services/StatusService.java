package dev.daae.time.services;

import dev.daae.time.LogRepository;
import dev.daae.time.models.Log;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusService {

  private final LogRepository logRepository;

  private final Clock clock;

  public String currentStatus() {
    var optionalLatest = logRepository.findFirstByOrderByTimestampDesc();
    if (optionalLatest.isEmpty()) {
      return "No logs in database.";
    }

    var latest = optionalLatest.get();

    if (latest.getKind() == Log.Kind.START) {
      var start = latest.getTimestamp();
      var end = OffsetDateTime.now(clock);
      var duration = Duration.between(start, end);
      var formattedDuration = formatDuration(duration);
      return "In progress, " + formattedDuration + ".";
    }

    var first2List = logRepository.findFirst2ByOrderByTimestampDesc();
    if (first2List.size() != 2 || first2List.get(1).getKind() == Log.Kind.STOP) {
      return "There is something wrong with the integrity of the log table.";
    }

    var secondLatest = first2List.get(1);

    var start = secondLatest.getTimestamp();
    var end = latest.getTimestamp();
    var duration = Duration.between(start, end);
    var formattedDuration = formatDuration(duration);
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
