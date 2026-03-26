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

import java.util.Locale;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl extends RegistrationServiceGrpc.RegistrationServiceImplBase {

    private final PersonServiceClient psClient;
    private final AddressServiceClient asClient;

    @Override
    public void getRegistration(RegistrationRequest request, StreamObserver<RegistrationResponse> responseObserver) {
        log.info("Received request for getRegistration() for id {}...", request.getId());
        Faker faker = new Faker(Locale.ENGLISH);

        Address address = asClient.execute(request.getId()).getAddress();
        Person person = psClient.execute(request.getId()).getPerson();

        RegistrationResponse response = RegistrationResponse.newBuilder()
                .setId(request.getId())
                .setEventName(faker.word().adjective() + " " + faker.company().buzzword() + " " + faker.company().catchPhrase())
                .setWantsToReceiveNewsletter(faker.bool().bool())
                .setAddress(address)
                .setPerson(person)
                .build();

        responseObserver.onNext(response);
        log.info("Successfully sent RegistrationResponse for id {}", request.getId());
        responseObserver.onCompleted();
    }

}
