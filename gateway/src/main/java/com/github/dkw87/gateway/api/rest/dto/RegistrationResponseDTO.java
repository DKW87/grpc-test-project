package com.github.dkw87.gateway.api.rest.dto;

import com.github.dkw87.grpc.proto.registration.RegistrationResponse;

public record RegistrationResponseDTO(
        long id,
        String eventName,
        boolean wantsToReceiveNewsletter,
        PersonResponseDTO person,
        AddressResponseDTO address
) {
    public static RegistrationResponseDTO from(RegistrationResponse response) {
        return new RegistrationResponseDTO(
                response.getId(),
                response.getEventName(),
                response.getWantsToReceiveNewsletter(),
                PersonResponseDTO.from(response.getPerson()),
                AddressResponseDTO.from(response.getAddress())
        );
    }
}
