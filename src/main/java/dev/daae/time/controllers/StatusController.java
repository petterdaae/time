package dev.daae.time.controllers;

import dev.daae.time.services.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/current")
    public String currentStatus() {
        return statusService.currentStatus();
    }

    @GetMapping("/week")
    public String currentWeek() {
        return statusService.weekStatus();
    }

    @GetMapping("/today")
    public String today() {
        return statusService.todayStatus();
    }
}
