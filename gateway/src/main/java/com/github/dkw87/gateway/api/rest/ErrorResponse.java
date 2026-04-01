package com.github.dkw87.gateway.api.rest;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        String traceId,
        List<Detail> details
) {
    public record Detail(String type, String message) {}
}
