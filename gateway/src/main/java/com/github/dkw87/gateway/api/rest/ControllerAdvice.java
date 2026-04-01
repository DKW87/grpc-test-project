package com.github.dkw87.gateway.api.rest;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        List<ErrorResponse.Violation> violations = e.getConstraintViolations().stream().map(
                violation -> new ErrorResponse.Violation(
                        violation.getPropertyPath().toString().replaceAll(".*\\.", ""),
                        violation.getMessage()
                )
        ).toList();

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        "Validation failed",
                        LocalDateTime.now(),
                        violations
                )
        );
    }

    @ExceptionHandler(InvalidProtocolBufferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProtocolBufferException(InvalidProtocolBufferException e) {
        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "The following error occured when creating a JSON response: " + e.getMessage(),
                        LocalDateTime.now(),
                        null
                )
        );
    }

}
