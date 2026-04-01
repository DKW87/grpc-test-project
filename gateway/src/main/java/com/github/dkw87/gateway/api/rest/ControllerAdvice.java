package com.github.dkw87.gateway.api.rest;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        List<ErrorResponse.Detail> details = e.getConstraintViolations().stream().map(
                violation -> new ErrorResponse.Detail(
                        "validation violation",
                        violation.getPropertyPath().toString().replaceAll(".*\\.", "")
                                + " " +violation.getMessage()
                )
        ).toList();

        log.warn("Bad Request did not pass validation: {} ", details);

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        "Validation failed",
                        LocalDateTime.now(),
                        details
                )
        );
    }

    @ExceptionHandler(InvalidProtocolBufferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProtocolBufferException(InvalidProtocolBufferException e) {
        log.error("InvalidProtocolBufferException occurred: ", e);

        ErrorResponse.Detail detail = new ErrorResponse.Detail(
                "exception",
                e.getMessage()
        );

        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "Could not print JSON response",
                        LocalDateTime.now(),
                        List.of(detail)
                )
        );
    }

}
