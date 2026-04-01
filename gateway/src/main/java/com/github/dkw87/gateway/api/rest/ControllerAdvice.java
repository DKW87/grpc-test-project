package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.rest.util.TraceIdGenerator;
import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvice {

    private final TraceIdGenerator traceIdGenerator;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        final String traceId = traceIdGenerator.generate();

        final List<ErrorResponse.Detail> details = e.getConstraintViolations().stream().map(
                violation -> new ErrorResponse.Detail(
                        "validation violation",
                        violation.getPropertyPath().toString().replaceAll(".*\\.", "")
                                + " " +violation.getMessage()
                )
        ).toList();

        log.warn("Bad Request with traceId({}) did not pass validation: {} ",traceId, details);

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        "Validation failed",
                        LocalDateTime.now(),
                        traceId,
                        details
                )
        );
    }

    @ExceptionHandler(InvalidProtocolBufferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProtocolBufferException(InvalidProtocolBufferException e) {
        final String traceId = traceIdGenerator.generate();
        log.error("InvalidProtocolBufferException for traceId({}) occurred: ", traceId, e);

        final ErrorResponse.Detail detail = new ErrorResponse.Detail(
                "exception",
                e.getMessage()
        );

        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "Could not print JSON response",
                        LocalDateTime.now(),
                        traceId,
                        List.of(detail)
                )
        );
    }

}
