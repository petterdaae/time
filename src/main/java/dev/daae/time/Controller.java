package dev.daae.time;

import dev.daae.time.models.*;
import dev.daae.time.repository.SessionRepository;
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

  private final StatusService statusService;

  private final SessionRepository sessionRepository;

  @GetMapping("/status/current")
  public String currentStatus() {
    return statusService.currentStatus();
  }

  @PostMapping("/session")
  @ResponseStatus(HttpStatus.CREATED)
  public String createLog() {
    var session = sessionRepository.findFirstByOrderByStartDesc();
    var now = LocalDateTime.now().atOffset(ZoneOffset.UTC);

    if (session.isEmpty() || session.get().getEnd().isPresent()) {
      var newSession = Session.newWithStartTime(now);
      sessionRepository.save(newSession);
      return "Started.";
    }

    session.get().setEnd(now);
    sessionRepository.save(session.get());
    return "Stopped.";
  }

  @GetMapping("/session")
  public List<Session> getAllSessions() {
    return sessionRepository.findAll();
  }

  @DeleteMapping("/session")
  public String deleteAllSessions() {
    sessionRepository.deleteAll();
    return "All sessions deleted.";
  }
}
