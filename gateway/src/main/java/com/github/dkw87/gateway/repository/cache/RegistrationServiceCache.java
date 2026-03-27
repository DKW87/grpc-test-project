package com.github.dkw87.gateway.repository.cache;

import com.github.dkw87.grpc.proto.registration.RegistrationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RegistrationServiceCache {

    private final ConcurrentHashMap<Long, RegistrationResponse> CACHE_MAP = new ConcurrentHashMap<>();

    public RegistrationResponse getFromCache(long id) {
        final RegistrationResponse registration = CACHE_MAP.get(id);
        final String logLine = (registration == null)
                ? "No RegistrationResponse found in cache for id {} "
                : "Found RegistrationResponse in cache for id {} ";

        log.info(logLine, id);
        return registration;
    }

    public void putInCache(RegistrationResponse response) {
        log.info("Putting RegistrationResponse with id {} in cache ", response.getId());
        CACHE_MAP.put(response.getId(), response);
    }

}
