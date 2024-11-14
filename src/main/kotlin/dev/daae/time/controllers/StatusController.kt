package dev.daae.time.controllers

import dev.daae.time.services.StatusService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/status")
class StatusController(
    private val statusService: StatusService,
) {
    @GetMapping("/current")
    fun currentStatus(): String {
        return statusService!!.currentStatus()
    }

    @GetMapping("/week")
    fun currentWeek(): String {
        return statusService!!.weekStatus()
    }

    @GetMapping("/today")
    fun today(): String {
        return statusService!!.todayStatus()
    }
}
