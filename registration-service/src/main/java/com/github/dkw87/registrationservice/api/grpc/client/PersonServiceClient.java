package com.github.dkw87.registrationservice.api.grpc.client;

import com.github.dkw87.grpc.proto.person.PersonRequest;
import com.github.dkw87.grpc.proto.person.PersonResponse;
import com.github.dkw87.grpc.proto.person.PersonServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonServiceClient {

    private final PersonServiceGrpc.PersonServiceBlockingStub stub;

    public PersonServiceClient(GrpcChannelFactory channelFactory) {
        this.stub = PersonServiceGrpc.newBlockingStub(channelFactory.createChannel("person-service"));
    }

    public PersonResponse execute(long id) {
        log.info("Requesting response from PersonServiceGrpc for id {}...", id);
        final PersonRequest request = PersonRequest.newBuilder().setId(id).build();
        return stub.getPerson(request);
    }

}
