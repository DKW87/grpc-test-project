package com.github.dkw87.gateway.repository.cache;

import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationServiceCache {

    private final static ConcurrentHashMap<Long, RegistrationResponse> CACHE_MAP = new ConcurrentHashMap<>();

    public RegistrationResponse getFromCache(long id) {
        return CACHE_MAP.get(id);
    }

    public void putInCache(RegistrationResponse response) {
        CACHE_MAP.put(response.getId(), response);
    }

}
