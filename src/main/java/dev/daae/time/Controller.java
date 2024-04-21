package dev.daae.time;

import dev.daae.time.models.CreateLogRequest;
import dev.daae.time.models.CreateLogResponse;
import dev.daae.time.models.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final LogRepository logRepository;

    @GetMapping("/status")
    public String status() {
        return "OK";
    }

    @PostMapping("/log")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateLogResponse createLog(@RequestBody CreateLogRequest createLogRequest) {
        var log = logRepository.save(
                Log.builder()
                        .kind(createLogRequest.kind())
                        .timestamp(LocalDateTime.now().atOffset(ZoneOffset.UTC))
                        .build()
        );
        return new CreateLogResponse(log.getId());
    }
}
