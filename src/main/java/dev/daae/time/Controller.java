package dev.daae.time;

import dev.daae.time.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final LogRepository logRepository;

    @GetMapping("/status")
    public StatusResponse status() {
        var logs = logRepository.findAllByOrderByTimestampDesc();

        var latest = logs.stream().findFirst();
        var latestKind = latest.map(Log::getKind).orElse(Log.Kind.STOP);
        var status = latestKind == Log.Kind.STOP ? "Stopped" : "Started";

        var secondLatest = logs.stream().skip(1).findFirst();
        var difference = latest.map(Log::getTimestamp).flatMap(latestTimestamp ->
                secondLatest
                        .map(Log::getTimestamp)
                        .map(secondLatestTimestamp -> Duration.between(secondLatestTimestamp, latestTimestamp))
        );
        var formattedDifference = difference
                .map(d -> String.format("%02d:%02d:%02d", d.toHours(), d.toMinutesPart(), d.toSecondsPart()))
                .orElse(null);

        return StatusResponse.builder()
                .status(status)
                .stats(
                        StatusResponseStats.builder()
                                .previous(formattedDifference)
                                .build()
                )
                .build();
    }

    @PostMapping("/log")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateLogResponse createLog() {
        var logs = logRepository.findAllByOrderByTimestampDesc();
        var latestKind = logs.stream().findFirst().map(Log::getKind).orElse(Log.Kind.STOP);
        var nextKind = latestKind == Log.Kind.STOP ? Log.Kind.START : Log.Kind.STOP;
        var log = logRepository.save(
                Log.builder()
                        .kind(nextKind)
                        .timestamp(LocalDateTime.now().atOffset(ZoneOffset.UTC))
                        .build()
        );
        return new CreateLogResponse(log.getId());
    }

    @GetMapping("/log")
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
}
