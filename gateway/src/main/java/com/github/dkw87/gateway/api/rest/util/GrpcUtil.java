package com.github.dkw87.gateway.api.rest.util;

import io.grpc.Status;
import org.springframework.stereotype.Component;

@Component
public class GrpcUtil {

    public boolean isGrpcWarnLevel(Status.Code code) {
        return switch (code) {
            case INVALID_ARGUMENT, NOT_FOUND, ALREADY_EXISTS, PERMISSION_DENIED,
                 UNAUTHENTICATED, CANCELLED, FAILED_PRECONDITION, OUT_OF_RANGE -> true;
            default -> false;
        };
    }

    public int mapGrpcToHttp(Status.Code code) {
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
