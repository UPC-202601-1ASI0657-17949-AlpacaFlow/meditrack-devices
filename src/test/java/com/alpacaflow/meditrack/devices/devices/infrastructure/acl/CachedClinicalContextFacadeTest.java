package com.alpacaflow.meditrack.devices.devices.infrastructure.acl;

import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.PatientThresholdSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedClinicalContextFacadeTest {

    @Mock
    private RemoteClinicalContextFacade remoteClinicalContextFacade;

    private CachedClinicalContextFacade cachedClinicalContextFacade;

    private static final PatientThresholdSnapshot THRESHOLDS =
            new PatientThresholdSnapshot(55, 95, 92, 35.5, 37.8);

    @BeforeEach
    void setUp() {
        cachedClinicalContextFacade = new CachedClinicalContextFacade(remoteClinicalContextFacade, 120);
    }

    @Test
    void shouldFetchFromRemoteAndCacheOnFirstRequest() {
        when(remoteClinicalContextFacade.fetchPatientThreshold(7L)).thenReturn(Optional.of(THRESHOLDS));

        var first = cachedClinicalContextFacade.fetchPatientThreshold(7L);
        var second = cachedClinicalContextFacade.fetchPatientThreshold(7L);

        assertTrue(first.isPresent());
        assertEquals(THRESHOLDS, first.get());
        assertEquals(THRESHOLDS, second.get());
        verify(remoteClinicalContextFacade, times(1)).fetchPatientThreshold(7L);
    }

    @Test
    void shouldServeStaleSnapshotWhenRemoteReturnsEmptyAfterCacheHit() {
        when(remoteClinicalContextFacade.fetchPatientThreshold(7L))
                .thenReturn(Optional.of(THRESHOLDS))
                .thenReturn(Optional.empty());

        cachedClinicalContextFacade.fetchPatientThreshold(7L);
        var degraded = cachedClinicalContextFacade.fetchPatientThreshold(7L);

        assertTrue(degraded.isPresent());
        assertEquals(THRESHOLDS, degraded.get());
    }

    @Test
    void shouldReturnEmptyWhenRemoteHasNoThresholdAndNoCache() {
        when(remoteClinicalContextFacade.fetchPatientThreshold(9L)).thenReturn(Optional.empty());

        var result = cachedClinicalContextFacade.fetchPatientThreshold(9L);

        assertTrue(result.isEmpty());
    }
}
