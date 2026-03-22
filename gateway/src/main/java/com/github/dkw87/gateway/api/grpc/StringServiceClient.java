package com.github.dkw87.gateway.api.grpc;

import com.github.dkw87.grpc.proto.StringResponse;
import com.github.dkw87.grpc.proto.StringServiceGrpc;
import com.google.protobuf.Empty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringServiceClient {

    // TODO: replace for non-blocking stub once initial project is up and running
    private final StringServiceGrpc.StringServiceBlockingStub stub;

    public StringServiceClient(GrpcChannelFactory channelFactory) {
        this.stub = StringServiceGrpc.newBlockingStub(channelFactory.createChannel("service"));
    }

    public StringResponse execute() {
        log.info("Executing StringServiceClient...");
        return stub.getString(Empty.getDefaultInstance());
    }

}