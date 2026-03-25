package com.github.dkw87.gateway.api.grpc;

import com.github.dkw87.gateway.repository.cache.RegistrationServiceCache;
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
    private final RegistrationServiceCache cache;

    public RegistrationServiceClient(GrpcChannelFactory channelFactory, RegistrationServiceCache cache) {
        this.stub = RegistrationServiceGrpc.newBlockingStub(channelFactory.createChannel("registration-service"));
        this.cache = cache;
    }

    public RegistrationResponse execute(long id) {
        log.info("Requesting response from RegistrationServiceGrpc for id {}...", id);
        RegistrationResponse cachedResponse = cache.getFromCache(id);

        if (cachedResponse != null) {
            return cachedResponse;
        }

        RegistrationRequest request = RegistrationRequest.newBuilder().setId(id).build();

        RegistrationResponse queriedResponse = stub.getRegistration(request);

        cache.putInCache(queriedResponse);

        return queriedResponse;
    }

}
