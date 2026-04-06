package com.github.dkw87.gateway.api.rest.dto;

import com.github.dkw87.grpc.proto.person.Person;

import java.util.List;

public record PersonResponseDTO(
        String name,
        String gender,
        String dob,
        String pob,
        List<String> hobbies
) {
    public static PersonResponseDTO from(Person person) {
        return new PersonResponseDTO(
                person.getName(),
                person.getGender(),
                person.getDob(),
                person.getPob(),
                person.getHobbiesList()
        );
    }
}
