package dev.daae.time.services;

import dev.daae.time.LogRepository;
import dev.daae.time.models.Log;
import dev.daae.time.models.StatusResponse;
import dev.daae.time.models.StatusResponseStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final LogRepository logRepository;

    public StatusResponse getStatus() {
        var logs = logRepository.findAllByOrderByTimestampDesc();

        var status = this.status(logs);
        var previous = this.getPreviousInterval(logs);

        return StatusResponse.builder()
                .status(status)
                .stats(
                        StatusResponseStats.builder()
                                .previous(previous)
                                .build()
                )
                .build();
    }

    private String status(List<Log> logs) {
        var latest = logs.stream().findFirst();
        var latestKind = latest.map(Log::getKind).orElse(Log.Kind.STOP);
        return latestKind == Log.Kind.STOP ? "Stopped" : "Started";
    }

    private String getPreviousInterval(List<Log> logs) {
        var latest = logs.stream().findFirst();
        var secondLatest = logs.stream().skip(1).findFirst();

        var latestTimestamp = latest.map(Log::getTimestamp);
        var secondLatestTimestamp = secondLatest.map(Log::getTimestamp);

        var difference = latestTimestamp.flatMap(t1 -> secondLatestTimestamp.map(t0 -> Duration.between(t0, t1)));

        return difference
                .map(d -> String.format("%02d:%02d:%02d", d.toHours(), d.toMinutesPart(), d.toSecondsPart()))
                .orElse(null);
    }
}
