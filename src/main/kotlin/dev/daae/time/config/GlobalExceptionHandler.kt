package dev.daae.time.config

import dev.daae.time.models.InternalServerErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<InternalServerErrorResponse> {
        logger.error(e) { e.message }
        return ResponseEntity<InternalServerErrorResponse>(
            InternalServerErrorResponse("Internal server error"),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }
}
