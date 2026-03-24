package com.github.dkw87.gateway.api.grpc;

import com.github.dkw87.grpc.proto.registration.RegistrationRequest;
import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import com.github.dkw87.grpc.proto.registration.RegistrationServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegistrationServiceClient {

    private final RegistrationServiceGrpc.RegistrationServiceBlockingStub stub;

    public RegistrationServiceClient(GrpcChannelFactory channelFactory) {
        this.stub = RegistrationServiceGrpc.newBlockingStub(channelFactory.createChannel("registration-service"));
    }

    public RegistrationResponse execute(long id) {
        log.info("Requesting response from RegistrationServiceGrpc for id {}...", id);
        RegistrationRequest request = RegistrationRequest.newBuilder().setId(id).build();
        return stub.getRegistration(request);
    }

}
