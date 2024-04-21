package dev.daae.time;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequiredArgsConstructor
public class Controller {

    private LogRepository logRepository;

    @GetMapping("/status")
    public String status() {
        return "OK";
    }

    @PostMapping("/log")
    @ResponseStatus(HttpStatus.CREATED)
    public String createLog(@RequestBody CreateLogRequest createLogRequest) {
        logRepository.save(
                Log.builder()
                        .description(createLogRequest.description())
                        .timestamp(LocalDateTime.now().atOffset(ZoneOffset.UTC))
                        .build()
        );
        return "CREATED";
    }
}
