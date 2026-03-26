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

    private final PersonServiceClient psClient;
    private final AddressServiceClient asClient;

    @Override
    public void getRegistration(RegistrationRequest request, StreamObserver<RegistrationResponse> responseObserver) {
        log.info("Received request for getRegistration() for id {}...", request.getId());
        final Faker faker = new Faker(Locale.ENGLISH);

        final Address address = asClient.execute(request.getId()).getAddress();
        final Person person = psClient.execute(request.getId()).getPerson();

        final String adjective = StringUtils.capitalize(faker.word().adjective());
        final String hobby = capitalizeWords(faker.hobby().activity());
        final String adverb = StringUtils.capitalize(faker.word().adverb());

        final RegistrationResponse response = RegistrationResponse.newBuilder()
                .setId(request.getId())
                .setEventName(String.format("%s %s %s", adjective,hobby,adverb))
                .setWantsToReceiveNewsletter(faker.bool().bool())
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

