package dev.daae.time;

import dev.daae.time.models.InternalServerErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<InternalServerErrorResponse> handleException(Exception e) {
    return new ResponseEntity<>(
        new InternalServerErrorResponse("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
