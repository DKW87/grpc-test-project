package com.github.dkw87.registrationservice.api.grpc.client;

import com.github.dkw87.grpc.proto.person.PersonRequest;
import com.github.dkw87.grpc.proto.person.PersonResponse;
import com.github.dkw87.grpc.proto.person.PersonServiceGrpc;
import io.grpc.Context;
import io.grpc.Deadline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PersonServiceClient {

    private final PersonServiceGrpc.PersonServiceBlockingStub stub;

    public PersonServiceClient(GrpcChannelFactory channelFactory) {
        this.stub = PersonServiceGrpc.newBlockingStub(channelFactory.createChannel("person-service"));
    }

    public PersonResponse execute(long id) {
        final PersonRequest request = PersonRequest.newBuilder().setId(id).build();
        final Deadline incoming = Context.current().getDeadline();
        final Deadline fallback = Deadline.after(4, TimeUnit.SECONDS);
        final Deadline deadline = incoming != null ? incoming : fallback;

        log.info("Requesting response from PersonServiceGrpc for id {}...", id);
        return stub.withDeadline(deadline).getPerson(request);
    }

}
