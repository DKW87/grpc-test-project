package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.rest.util.TraceIdGenerator;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvice {

    private final TraceIdGenerator traceIdGenerator;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        final List<ErrorResponse.Detail> details = e.getConstraintViolations().stream().map(
                violation -> new ErrorResponse.Detail(
                        "violation",
                        violation.getPropertyPath().toString().replaceAll(".*\\.", "")
                                + " " +violation.getMessage()
                )
        ).toList();

        final String traceId = traceIdGenerator.generate();
        log.warn("Bad Request with traceId({}) did not pass validation: {} ",traceId, details);

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Bad Request",
                        "Validation failed",
                        traceId,
                        details
                )
        );
    }

    @ExceptionHandler(InvalidProtocolBufferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProtocolBufferException(InvalidProtocolBufferException e) {
        final String traceId = traceIdGenerator.generate();
        log.error("InvalidProtocolBufferException occurred trying to print JSON response with traceId({}): ", traceId, e);

        final ErrorResponse.Detail detail = new ErrorResponse.Detail(
                "exception",
                e.getMessage()
        );

        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "Could not print JSON response",
                        traceId,
                        List.of(detail)
                )
        );
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleStatusRuntimeException(StatusRuntimeException e) {
        final Status.Code code = e.getStatus().getCode();
        final String description = e.getStatus().getDescription();
        final String traceId = traceIdGenerator.generate();

        if (isGrpcWarnLevel(code)) {
            log.warn("gRPC warning occurred with traceId({}): {}", traceId, code.name(), e);
        } else {
            log.error("gRPC error occurred with traceId({}): {}", traceId, code.name(), e);
        }

        return ResponseEntity.status(mapGrpcToHttp(code)).body(
                new ErrorResponse(
                        mapGrpcToHttp(code),
                        "gRPC Error",
                        code.name(),
                        traceId,
                        List.of(new ErrorResponse.Detail("description", description))
                )
        );
    }

    private boolean isGrpcWarnLevel(Status.Code code) {
        return switch (code) {
            case INVALID_ARGUMENT, NOT_FOUND, ALREADY_EXISTS, PERMISSION_DENIED,
                 UNAUTHENTICATED, CANCELLED, FAILED_PRECONDITION, OUT_OF_RANGE -> true;
            default -> false;
        };
    }

    private int mapGrpcToHttp(Status.Code code) {
        return switch (code) {
            case INVALID_ARGUMENT, FAILED_PRECONDITION, OUT_OF_RANGE -> 400;
            case NOT_FOUND -> 404;
            case ALREADY_EXISTS -> 409;
            case UNAUTHENTICATED -> 401;
            case PERMISSION_DENIED -> 403;
            case RESOURCE_EXHAUSTED -> 429;
            case UNIMPLEMENTED -> 501;
            case UNAVAILABLE, DEADLINE_EXCEEDED -> 503;
            default -> 500;
        };
    }

}
