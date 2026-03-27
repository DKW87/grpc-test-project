package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.grpc.RegistrationServiceClient;
import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
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

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer().alwaysPrintFieldsWithNoPresence();

    private final RegistrationServiceClient registrationServiceClient;

    @GetMapping("/{id}")
    public String getId(@PathVariable("id") long id) {
        log.info("Executing RegistrationServiceClient for id {}...", id);
        final RegistrationResponse response = registrationServiceClient.execute(id);
        String registration = null;

        try {
            registration = JSON_PRINTER.print(response);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error while creating JSON response for {} \n", id, e);
        }

        log.info("Response from RegistrationServiceClient: {}", registration);
        return registration;
    }

}
