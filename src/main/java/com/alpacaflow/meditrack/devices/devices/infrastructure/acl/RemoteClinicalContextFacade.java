package com.alpacaflow.meditrack.devices.devices.infrastructure.acl;

import com.alpacaflow.meditrack.devices.devices.application.acl.ClinicalContextFacade;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.PatientThresholdSnapshot;
import com.alpacaflow.meditrack.devices.devices.infrastructure.acl.client.RemotePatientThresholdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class RemoteClinicalContextFacade implements ClinicalContextFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClinicalContextFacade.class);

    private final RestClient restClient;

    public RemoteClinicalContextFacade(@Value("${app.clinical.base-url}") String baseUrl) {
        var normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.restClient = RestClient.builder().baseUrl(normalizedBaseUrl).build();
    }

    @Override
    public Optional<PatientThresholdSnapshot> fetchPatientThreshold(Long seniorCitizenId) {
        if (seniorCitizenId == null || seniorCitizenId <= 0) {
            return Optional.empty();
        }

        try {
            var response = restClient.get()
                    .uri("/api/v1/patient-thresholds/senior-citizen/{seniorCitizenId}", seniorCitizenId)
                    .retrieve()
                    .body(RemotePatientThresholdResponse.class);

            if (response == null) {
                return Optional.empty();
            }

            return Optional.of(new PatientThresholdSnapshot(
                    response.minBpm(),
                    response.maxBpm(),
                    response.minSpo2(),
                    response.minCelsius(),
                    response.maxCelsius()
            ));
        } catch (HttpClientErrorException.NotFound notFound) {
            LOGGER.debug("No patient threshold configured yet for seniorCitizenId={}", seniorCitizenId);
            return Optional.empty();
        } catch (Exception exception) {
            LOGGER.warn("Unable to fetch patient threshold for seniorCitizenId={}: {}",
                    seniorCitizenId, exception.getMessage());
            return Optional.empty();
        }
    }
}
