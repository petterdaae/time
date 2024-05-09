package dev.daae.time;

import dev.daae.time.models.*;
import dev.daae.time.services.StatusService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class Controller {

  private final LogRepository logRepository;

  private final StatusService statusService;

  @GetMapping("/status/current")
  public String currentStatus() {
    return statusService.currentStatus();
  }

  @PostMapping("/log")
  @ResponseStatus(HttpStatus.CREATED)
  public String createLog() {
    var logs = logRepository.findAllByOrderByTimestampDesc();
    var latestKind = logs.stream().findFirst().map(Log::getKind).orElse(Log.Kind.STOP);
    var nextKind = latestKind == Log.Kind.STOP ? Log.Kind.START : Log.Kind.STOP;
    logRepository.save(
        Log.builder()
            .kind(nextKind)
            .timestamp(LocalDateTime.now().atOffset(ZoneOffset.UTC))
            .build());
    return nextKind == Log.Kind.START ? "Started." : "Stopped.";
  }

  @GetMapping("/log")
  public List<Log> getAllLogs() {
    return logRepository.findAll();
  }
}
