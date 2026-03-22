package com.github.dkw87.service.api.grpc;

import com.github.dkw87.grpc.proto.StringResponse;
import com.github.dkw87.grpc.proto.StringServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class StringServiceImpl extends StringServiceGrpc.StringServiceImplBase {

    @Override
    public void getString(Empty request, StreamObserver<StringResponse> responseObserver) {
        log.info("Received request for getString() and building response...");

        StringResponse response = StringResponse.newBuilder()
                .setStringValue("Hi, successfully called a gRPC service. Good job! :)")
                .build();

        log.info("Response build and sending response...");
        responseObserver.onNext(response);

        responseObserver.onCompleted();
        log.info("Completed request for getString()");
    }

}