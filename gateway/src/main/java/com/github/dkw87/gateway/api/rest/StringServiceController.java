package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.grpc.StringServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/string")
@RequiredArgsConstructor
@Slf4j
public class StringServiceController {

    private final StringServiceClient client;

    @GetMapping
    public String string() {
        log.info("Executing StringServiceClient...");
        String value = client.execute().getStringValue();

        log.info("Response from StringServiceClient: {}", value);
        return value;
    }

}
