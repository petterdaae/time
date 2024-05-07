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

  @GetMapping("/status")
  public StatusResponse status() {
    return statusService.getStatus();
  }

  @PostMapping("/log")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateLogResponse createLog() {
    var logs = logRepository.findAllByOrderByTimestampDesc();
    var latestKind = logs.stream().findFirst().map(Log::getKind).orElse(Log.Kind.STOP);
    var nextKind = latestKind == Log.Kind.STOP ? Log.Kind.START : Log.Kind.STOP;
    var log =
        logRepository.save(
            Log.builder()
                .kind(nextKind)
                .timestamp(LocalDateTime.now().atOffset(ZoneOffset.UTC))
                .build());
    return new CreateLogResponse(log.getId());
  }

  @GetMapping("/log")
  public List<Log> getAllLogs() {
    return logRepository.findAll();
  }
}
