package com.github.dkw87.addressservice.api.grpc;

import com.github.dkw87.grpc.proto.address.Address;
import com.github.dkw87.grpc.proto.address.AddressRequest;
import com.github.dkw87.grpc.proto.address.AddressResponse;
import com.github.dkw87.grpc.proto.address.AddressServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class AddressServiceImpl extends AddressServiceGrpc.AddressServiceImplBase {

    @Override
    public void getAddress(AddressRequest request, StreamObserver<AddressResponse> responseObserver) {
        log.info("Received request for getAddress() for id {}...", request.getId());

        Address address = Address.newBuilder()
                .setStreetAndNumber("10 Downing Street")
                .setZip("SW1A 2AA")
                .setCity("City of Westminster London")
                .setCountry("United Kingdom")
                .build();

        AddressResponse response  = AddressResponse.newBuilder()
                .setAddress(address)
                .build();

        responseObserver.onNext(response);
        log.info("Successfully sent AddressResponse for id {}", request.getId());
        responseObserver.onCompleted();

    }

}
