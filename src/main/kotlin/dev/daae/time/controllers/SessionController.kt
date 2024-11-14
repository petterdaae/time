package dev.daae.time.controllers

import dev.daae.time.models.Session
import dev.daae.time.models.Session.Companion.newWithStartTime
import dev.daae.time.models.UpdateSessionRequest
import dev.daae.time.repositories.SessionRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@RestController
@RequestMapping("/session")
class SessionController (
    private val sessionRepository: SessionRepository
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createLog(): String {
        val session = sessionRepository!!.findFirstByOrderByStartDesc()
        val now = LocalDateTime.now().atOffset(ZoneOffset.UTC)

        if (session == null || session.end != null) {
            val newSession = newWithStartTime(now)
            sessionRepository.save<Session?>(newSession)
            return "Started."
        }

        session.end = now
        sessionRepository.save<Session?>(session)
        return "Stopped."
    }

    @DeleteMapping
    fun deleteAllSessions(): String {
        sessionRepository!!.deleteAll()
        return "All sessions deleted."
    }

    @PutMapping("/start")
    fun updateSessionStart(@RequestBody updateSessionRequest: UpdateSessionRequest): String {
        val session = sessionRepository!!.findFirstByOrderByStartDesc()
        if (session == null) {
            return "No session to update."
        }

        var start = session.start

        val plus = updateSessionRequest.plus ?: 0
        val minus = updateSessionRequest.minus ?: 0
        start = start!!.plusMinutes(plus.toLong())
        start = start.minusMinutes(minus.toLong())

        session.start = start
        sessionRepository.save<Session?>(session)

        return "Session start updated."
    }

    @PutMapping("/end")
    fun updateSessionEnd(@RequestBody updateSessionRequest: UpdateSessionRequest): String {
        val session = sessionRepository!!.findFirstByOrderByStartDesc()
        if (session == null) {
            return "No session to update."
        }

        val optionalEnd = session.end
        if (optionalEnd == null) {
            return "Can not update session end until session has ended."
        }
        var end: OffsetDateTime? = optionalEnd

        val plus = updateSessionRequest.plus ?: 0
        val minus = updateSessionRequest.minus ?: 0
        end = end!!.plusMinutes(plus.toLong())
        end = end.minusMinutes(minus.toLong())

        session.end = end
        sessionRepository.save<Session?>(session)

        return "Session end updated."
    }
}
