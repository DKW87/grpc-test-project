package com.github.dkw87.service;

import com.github.dkw87.grpc.proto.StringResponse;
import com.github.dkw87.grpc.proto.StringServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class StringServiceImpl extends StringServiceGrpc.StringServiceImplBase {

    @Override
    public void getString(Empty request, StreamObserver<StringResponse> responseObserver) {
        StringResponse response = StringResponse.newBuilder()
                .setStringValue("Hi, successfully called a gRPC service. Good job! :)")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}