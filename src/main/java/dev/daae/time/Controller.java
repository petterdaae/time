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

    @GetMapping("/status/week")
    public String currentWeek() {
        return statusService.weekStatus();
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

    @PutMapping("/session/start")
    public String updateSessionStart(@RequestBody UpdateSessionRequest updateSessionRequest) {
        var optionalSession = sessionRepository.findFirstByOrderByStartDesc();
        if (optionalSession.isEmpty()) {
            return "No session to update.";
        }

        var session = optionalSession.get();
        var start = session.getStart();

        var plus = updateSessionRequest.getPlus().orElse(0);
        var minus = updateSessionRequest.getMinus().orElse(0);
        start = start.plusMinutes(plus);
        start = start.minusMinutes(minus);

        session.setStart(start);
        sessionRepository.save(session);

        return "Session start updated.";
    }

    @PutMapping("/session/end")
    public String updateSessionEnd(@RequestBody UpdateSessionRequest updateSessionRequest) {
        var optionalSession = sessionRepository.findFirstByOrderByStartDesc();
        if (optionalSession.isEmpty()) {
            return "No session to update.";
        }

        var session = optionalSession.get();
        var optionalEnd = session.getEnd();
        if (optionalEnd.isEmpty()) {
            return "Can not update session end until session has ended.";
        }
        var end = optionalEnd.get();

        var plus = updateSessionRequest.getPlus().orElse(0);
        var minus = updateSessionRequest.getMinus().orElse(0);
        end = end.plusMinutes(plus);
        end = end.minusMinutes(minus);

        session.setEnd(end);
        sessionRepository.save(session);

        return "Session end updated.";
    }
}
