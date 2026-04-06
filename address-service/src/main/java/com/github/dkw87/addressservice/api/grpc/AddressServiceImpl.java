package com.github.dkw87.addressservice.api.grpc;

import com.github.dkw87.grpc.proto.address.Address;
import com.github.dkw87.grpc.proto.address.AddressRequest;
import com.github.dkw87.grpc.proto.address.AddressResponse;
import com.github.dkw87.grpc.proto.address.AddressServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Locale;

@GrpcService
@Slf4j
public class AddressServiceImpl extends AddressServiceGrpc.AddressServiceImplBase {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);
    private static final long SLEEP_IN_MILLIS = 6000;

    @Override
    public void getAddress(AddressRequest request, StreamObserver<AddressResponse> responseObserver) {
        log.info("Received request for getAddress() for id {}...", request.getId());

        possiblySlowResponse();

        final Address address = Address.newBuilder()
                .setStreetAndNumber(FAKER.address().streetName() + " " + FAKER.address().streetAddressNumber())
                .setZip(FAKER.address().zipCode())
                .setCity(FAKER.address().city())
                .setCountry(FAKER.address().country())
                .build();

        final AddressResponse response = AddressResponse.newBuilder()
                .setAddress(address)
                .build();

        responseObserver.onNext(response);
        log.info("Successfully sent AddressResponse for id {}", request.getId());
        responseObserver.onCompleted();

    }

    // simulates error response and propagates it upstream to showcase error handling on new registration query
    private void possiblySlowResponse() {
        if (FAKER.number().numberBetween(0, 10) == 0) {
            log.warn("Simulating connection timeout for {}ms...", SLEEP_IN_MILLIS);
            try {
                Thread.sleep(SLEEP_IN_MILLIS);
            } catch (InterruptedException e) {
                log.warn("Thread sleep interrupted");
            }
        }
    }

}
