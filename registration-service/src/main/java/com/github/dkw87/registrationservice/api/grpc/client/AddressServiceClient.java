package com.github.dkw87.registrationservice.api.grpc.client;

import com.github.dkw87.grpc.proto.address.AddressRequest;
import com.github.dkw87.grpc.proto.address.AddressResponse;
import com.github.dkw87.grpc.proto.address.AddressServiceGrpc;
import io.grpc.Context;
import io.grpc.Deadline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AddressServiceClient {

    private final AddressServiceGrpc.AddressServiceBlockingStub stub;

    public AddressServiceClient(GrpcChannelFactory channelFactory) {
        this.stub = AddressServiceGrpc.newBlockingStub(channelFactory.createChannel("address-service"));
    }

    public AddressResponse execute(long id) {
        final AddressRequest request = AddressRequest.newBuilder().setId(id).build();
        final Deadline incoming = Context.current().getDeadline();
        final Deadline fallback = Deadline.after(4, TimeUnit.SECONDS);
        final Deadline deadline = incoming != null ? incoming : fallback;

        log.info("Requesting response from AddressServiceGrpc for id {}...", id);
        return stub.withDeadline(deadline).getAddress(request);
    }

}
