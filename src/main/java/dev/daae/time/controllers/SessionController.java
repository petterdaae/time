package dev.daae.time.controllers;

import dev.daae.time.models.*;
import dev.daae.time.repositories.SessionRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/session")
public class SessionController {

    private final SessionRepository sessionRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createLog() {
        var session = sessionRepository.findFirstByOrderByStartDesc();
        var now = LocalDateTime.now().atOffset(ZoneOffset.UTC);

        if (session == null || session.getEnd() != null) {
            var newSession = Session.newWithStartTime(now);
            sessionRepository.save(newSession);
            return "Started.";
        }

        session.setEnd(now);
        sessionRepository.save(session);
        return "Stopped.";
    }

    @DeleteMapping
    public String deleteAllSessions() {
        sessionRepository.deleteAll();
        return "All sessions deleted.";
    }

    @PutMapping("/start")
    public String updateSessionStart(@RequestBody UpdateSessionRequest updateSessionRequest) {
        var session = sessionRepository.findFirstByOrderByStartDesc();
        if (session == null) {
            return "No session to update.";
        }

        var start = session.getStart();

        var plus = updateSessionRequest.getPlus() instanceof Integer p ? p : 0;
        var minus = updateSessionRequest.getMinus() instanceof Integer m ? m : 0;
        start = start.plusMinutes(plus);
        start = start.minusMinutes(minus);

        session.setStart(start);
        sessionRepository.save(session);

        return "Session start updated.";
    }

    @PutMapping("/end")
    public String updateSessionEnd(@RequestBody UpdateSessionRequest updateSessionRequest) {
        var session = sessionRepository.findFirstByOrderByStartDesc();
        if (session == null) {
            return "No session to update.";
        }

        var optionalEnd = session.getEnd();
        if (optionalEnd == null) {
            return "Can not update session end until session has ended.";
        }
        var end = optionalEnd;

        var plus = updateSessionRequest.getPlus() instanceof Integer p ? p : 0;
        var minus = updateSessionRequest.getMinus() instanceof Integer m ? m : 0;
        end = end.plusMinutes(plus);
        end = end.minusMinutes(minus);

        session.setEnd(end);
        sessionRepository.save(session);

        return "Session end updated.";
    }
}
