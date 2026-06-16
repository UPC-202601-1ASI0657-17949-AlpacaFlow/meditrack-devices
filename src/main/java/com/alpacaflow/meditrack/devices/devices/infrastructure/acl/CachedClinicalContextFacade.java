package com.alpacaflow.meditrack.devices.devices.infrastructure.acl;

import com.alpacaflow.meditrack.devices.devices.application.acl.ClinicalContextFacade;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.PatientThresholdSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ACL decorator: caches patient thresholds from clinical and serves stale values when clinical is unavailable.
 * Supports high-frequency IoT evaluation without hammering clinical on every measurement (performance + availability).
 */
@Component
@Primary
public class CachedClinicalContextFacade implements ClinicalContextFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedClinicalContextFacade.class);

    private final RemoteClinicalContextFacade remoteClinicalContextFacade;
    private final Duration cacheTtl;
    private final ConcurrentHashMap<Long, CacheEntry> cache = new ConcurrentHashMap<>();

    public CachedClinicalContextFacade(
            RemoteClinicalContextFacade remoteClinicalContextFacade,
            @Value("${app.clinical.cache-ttl-seconds:120}") long cacheTtlSeconds) {
        this.remoteClinicalContextFacade = remoteClinicalContextFacade;
        this.cacheTtl = Duration.ofSeconds(Math.max(cacheTtlSeconds, 30));
    }

    @Override
    public Optional<PatientThresholdSnapshot> fetchPatientThreshold(Long seniorCitizenId) {
        if (seniorCitizenId == null || seniorCitizenId <= 0) {
            return Optional.empty();
        }

        var cached = cache.get(seniorCitizenId);
        if (cached != null && cached.isFresh()) {
            return Optional.of(cached.snapshot());
        }

        var remoteResult = remoteClinicalContextFacade.fetchPatientThreshold(seniorCitizenId);
        if (remoteResult.isPresent()) {
            cache.put(seniorCitizenId, CacheEntry.fresh(remoteResult.get(), cacheTtl));
            return remoteResult;
        }

        if (cached != null) {
            LOGGER.warn(
                    "Clinical returned no threshold for seniorCitizenId={}; using last known snapshot (degraded)",
                    seniorCitizenId
            );
            return Optional.of(cached.snapshot());
        }

        return Optional.empty();
    }

    private record CacheEntry(PatientThresholdSnapshot snapshot, Instant expiresAt) {

        static CacheEntry fresh(PatientThresholdSnapshot snapshot, Duration ttl) {
            return new CacheEntry(snapshot, Instant.now().plus(ttl));
        }

        boolean isFresh() {
            return Instant.now().isBefore(expiresAt);
        }
    }
}
