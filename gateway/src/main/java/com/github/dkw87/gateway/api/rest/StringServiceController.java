package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.grpc.StringServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/string")
@RequiredArgsConstructor
public class StringServiceController {

    private final StringServiceClient client;

    @GetMapping
    public String string() {
        return client.execute().getStringValue();
    }

}
