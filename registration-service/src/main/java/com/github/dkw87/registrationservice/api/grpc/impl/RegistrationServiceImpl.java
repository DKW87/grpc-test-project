package com.github.dkw87.registrationservice.api.grpc.impl;

import com.github.dkw87.grpc.proto.address.Address;
import com.github.dkw87.grpc.proto.address.AddressResponse;
import com.github.dkw87.grpc.proto.person.Person;
import com.github.dkw87.grpc.proto.person.PersonResponse;
import com.github.dkw87.grpc.proto.registration.RegistrationRequest;
import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import com.github.dkw87.grpc.proto.registration.RegistrationServiceGrpc;
import com.github.dkw87.registrationservice.api.grpc.client.AddressServiceClient;
import com.github.dkw87.registrationservice.api.grpc.client.PersonServiceClient;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
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
        final Context context = Context.current();

        Address address;
        Person person;

        try {
            final CompletableFuture<AddressResponse> addressFuture = getAddressAsync(context, request.getId());
            final CompletableFuture<PersonResponse> personFuture = getPersonAsync(context, request.getId());
            CompletableFuture.allOf(addressFuture, personFuture).join();

            address = addressFuture.get().getAddress();
            person = personFuture.get().getPerson();
        } catch (ExecutionException | InterruptedException | CompletionException e) {
            handleException(responseObserver, request.getId(), e);
            return;
        }

        final String adjective = StringUtils.capitalize(FAKER.word().adjective());
        final String hobby = capitalizeEachWord(FAKER.hobby().activity());
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

    private CompletableFuture<AddressResponse> getAddressAsync(Context context, long id) {
        return CompletableFuture.supplyAsync(() -> {
            Context previousContext = context.attach();
            try {
                return addressServiceClient.execute(id);
            } finally {
                context.detach(previousContext);
            }
        });
    }

    private CompletableFuture<PersonResponse> getPersonAsync(Context context, long id) {
        return CompletableFuture.supplyAsync(() -> {
            Context previousContext = context.attach();
            try {
                return personServiceClient.execute(id);
            }  finally {
                context.detach(previousContext);
            }
        });
    }

    private void handleException(StreamObserver<RegistrationResponse> responseObserver, long id, Exception e) {
        log.error("Error while getting registration for id {}", id, e);

        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        final Status status = (cause instanceof StatusRuntimeException sre)
                ? sre.getStatus()
                : Status.INTERNAL;

        responseObserver.onError(status
                .withDescription(cause.getMessage())
                .withCause(cause)
                .asRuntimeException());
    }

    private String capitalizeEachWord(String words) {
        return Arrays.stream(words.split(" "))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

}

