package com.github.dkw87.gateway.api.rest.dto;

import com.github.dkw87.grpc.proto.address.Address;

public record AddressResponseDTO(
        String streetAndNumber,
        String zip,
        String city,
        String country
) {
    public static AddressResponseDTO from(Address address) {
        return new AddressResponseDTO(
                address.getStreetAndNumber(),
                address.getZip(),
                address.getCity(),
                address.getCountry()
        );
    }
}
