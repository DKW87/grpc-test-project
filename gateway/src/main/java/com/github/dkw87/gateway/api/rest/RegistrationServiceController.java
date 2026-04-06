package com.github.dkw87.gateway.api.rest;

import com.github.dkw87.gateway.api.grpc.RegistrationServiceClient;
import com.github.dkw87.gateway.api.rest.dto.RegistrationResponseDTO;
import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1/registrations")
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceController {

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer().alwaysPrintFieldsWithNoPresence();

    private final RegistrationServiceClient registrationServiceClient;

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponseDTO> getId(@PathVariable @Positive long id) throws InvalidProtocolBufferException {
        log.info("Executing RegistrationServiceClient for id {}...", id);

        final RegistrationResponse serviceResponse = registrationServiceClient.execute(id);

        final RegistrationResponseDTO clientResponse = RegistrationResponseDTO.from(serviceResponse);

        log.info("Response from RegistrationServiceClient: {}", clientResponse);
        return ResponseEntity.ok(clientResponse);
    }

}
