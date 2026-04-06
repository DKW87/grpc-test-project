package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.rest.util.GrpcUtil;
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
    private final GrpcUtil grpcUtil;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        final List<ErrorResponse.Detail> details = mapViolations(e);
        final String traceId = traceIdGenerator.generate();

        log.warn("Bad Request with traceId({}) did not pass validation: {} ", traceId, details);

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

    private List<ErrorResponse.Detail> mapViolations(ConstraintViolationException e) {
        return e.getConstraintViolations().stream().map(
                violation -> new ErrorResponse.Detail(
                        "violation",
                        violation.getPropertyPath().toString().replaceAll(".*\\.", "")
                                + " " + violation.getMessage()
                )
        ).toList();
    }

    @ExceptionHandler(InvalidProtocolBufferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProtocolBufferException(InvalidProtocolBufferException e) {
        final String traceId = traceIdGenerator.generate();

        log.error("InvalidProtocolBufferException occurred trying to print JSON response with traceId({}): ", traceId, e);

        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "Could not print JSON response",
                        traceId,
                        List.of(new ErrorResponse.Detail("exception", e.getMessage()))
                )
        );
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleStatusRuntimeException(StatusRuntimeException e) {
        final Status.Code code = e.getStatus().getCode();
        final String traceId = traceIdGenerator.generate();

        logStatusRuntimeException(e, traceId);

        return ResponseEntity.status(grpcUtil.mapGrpcToHttp(code)).body(
                new ErrorResponse(
                        grpcUtil.mapGrpcToHttp(code),
                        code.name(),
                        e.getStatus().getDescription(),
                        traceId,
                        List.of(new ErrorResponse.Detail("exception", e.getClass().getSimpleName()))
                )
        );
    }

    private void logStatusRuntimeException(StatusRuntimeException e, String traceId) {
        if (grpcUtil.isGrpcWarnLevel(e.getStatus().getCode())) {
            log.warn("gRPC warning: \"{}\" occurred with traceId({})", e.getMessage(), traceId, e);
        } else {
            log.error("gRPC error: \"{}\" occurred with traceId({})", e.getMessage(), traceId, e);
        }
    }

}
