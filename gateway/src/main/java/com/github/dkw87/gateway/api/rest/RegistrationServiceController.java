package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.grpc.RegistrationServiceClient;
import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/registrations")
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceController {

    private final RegistrationServiceClient rsclient;

    @GetMapping("/{id}")
    public String getId(@PathVariable("id") long id) {
        log.info("Executing RegistrationServiceClient for id {}...", id);
        RegistrationResponse response = rsclient.execute(id);

        String registration = response.toString();
        log.info("Response from RegistrationServiceClient: {}", registration);
        return registration;
    }

}
