package com.github.dkw87.gateway.api.rest.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TraceIdGenerator {

    private static final int LENGTH = 8;

    public String generate() {
        return UUID.randomUUID()
                .toString()
                .substring(0, LENGTH)
                .toUpperCase();
    }

}
