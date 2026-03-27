package com.github.dkw87.registrationservice.api.grpc.impl;

import com.github.dkw87.grpc.proto.address.Address;
import com.github.dkw87.grpc.proto.person.Person;
import com.github.dkw87.grpc.proto.registration.RegistrationRequest;
import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import com.github.dkw87.grpc.proto.registration.RegistrationServiceGrpc;
import com.github.dkw87.registrationservice.api.grpc.client.AddressServiceClient;
import com.github.dkw87.registrationservice.api.grpc.client.PersonServiceClient;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl extends RegistrationServiceGrpc.RegistrationServiceImplBase {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private final PersonServiceClient personServiceClient;
    private final AddressServiceClient addressServiceClient;

    @Override
    public void getRegistration(RegistrationRequest request, StreamObserver<RegistrationResponse> responseObserver) {
        log.info("Received request for getRegistration() for id {}...", request.getId());

        final Address address = addressServiceClient.execute(request.getId()).getAddress();
        final Person person = personServiceClient.execute(request.getId()).getPerson();

        final String adjective = StringUtils.capitalize(FAKER.word().adjective());
        final String hobby = capitalizeWords(FAKER.hobby().activity());
        final String adverb = StringUtils.capitalize(FAKER.word().adverb());

        final RegistrationResponse response = RegistrationResponse.newBuilder()
                .setId(request.getId())
                .setEventName(String.format("%s %s %s", adjective,hobby,adverb))
                .setWantsToReceiveNewsletter(FAKER.bool().bool())
                .setAddress(address)
                .setPerson(person)
                .build();

        responseObserver.onNext(response);
        log.info("Successfully sent RegistrationResponse for id {}", request.getId());
        responseObserver.onCompleted();
    }

    private String capitalizeWords(String words) {
        return Arrays.stream(words.split(" "))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

}

