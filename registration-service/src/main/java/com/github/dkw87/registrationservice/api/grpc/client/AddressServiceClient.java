package com.github.dkw87.registrationservice.api.grpc.client;

import com.github.dkw87.grpc.proto.address.AddressRequest;
import com.github.dkw87.grpc.proto.address.AddressResponse;
import com.github.dkw87.grpc.proto.address.AddressServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddressServiceClient {

    private final AddressServiceGrpc.AddressServiceBlockingStub stub;

    public AddressServiceClient(GrpcChannelFactory channelFactory) {
        this.stub = AddressServiceGrpc.newBlockingStub(channelFactory.createChannel("address-service"));
    }

    public AddressResponse execute(long id) {
        log.info("Requesting response from AddressServiceGrpc for id {}...", id);
        AddressRequest request = AddressRequest.newBuilder().setId(id).build();
        return stub.getAddress(request);
    }

}
