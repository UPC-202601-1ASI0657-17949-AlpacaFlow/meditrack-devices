package com.alpacaflow.meditrack.devices.application.internal.commandservices;

import com.alpacaflow.meditrack.devices.devices.application.internal.commandservices.DeviceCommandServiceImpl;
import com.alpacaflow.meditrack.devices.devices.application.internal.services.PatientThresholdResolver;
import com.alpacaflow.meditrack.devices.devices.domain.exceptions.DeviceNotFoundException;
import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Device;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.*;
import com.alpacaflow.meditrack.devices.devices.domain.model.entities.HeartRateMeasurement;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.PatientThresholdSnapshot;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeviceCommandServiceImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private PatientThresholdResolver patientThresholdResolver;

    @InjectMocks
    private DeviceCommandServiceImpl deviceCommandService;

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device("Watch", 1L);
        when(patientThresholdResolver.resolveForDevice(any(Device.class)))
                .thenReturn(PatientThresholdSnapshot.defaults());
    }

    @Test
    void shouldCreateDeviceSuccessfully() {

        var command = new CreateDeviceCommand("Watch", 1L);

        var savedDevice = new Device(command);

        ReflectionTestUtils.setField(savedDevice, "id", 1L);

        when(deviceRepository.saveAndFlush(any(Device.class)))
                .thenReturn(savedDevice);

        Long result = deviceCommandService.handle(command);

        assertEquals(1L, result);

        verify(deviceRepository, times(2))
                .saveAndFlush(any(Device.class));
    }

    @Test
    void shouldAddHeartRateMeasurementSuccessfully() {
        var command = new AddHeartRateMeasurementToDeviceCommand(80, 1L);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        when(deviceRepository.save(any(Device.class)))
                .thenReturn(device);

        var result = deviceCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(1,
                result.get().getHeartRateMeasurements().size());

        verify(deviceRepository).save(device);
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFound() {
        var command = new AddHeartRateMeasurementToDeviceCommand(80, 1L);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> deviceCommandService.handle(command));
    }

    @Test
    void shouldRemoveOldestMeasurementWhenAtCapacity() {
        for (int i = 0; i < Device.MAX_RETAINED_MEASUREMENTS_PER_TYPE; i++) {
            var measurement = new HeartRateMeasurement(70 + i);
            measurement.setMeasuredAt(LocalDateTime.now().minusMinutes(Device.MAX_RETAINED_MEASUREMENTS_PER_TYPE - i));
            device.addHeartRateMeasurement(measurement);
        }

        var command = new AddHeartRateMeasurementToDeviceCommand(200, 1L);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        when(deviceRepository.save(any(Device.class)))
                .thenReturn(device);

        deviceCommandService.handle(command);

        assertEquals(Device.MAX_RETAINED_MEASUREMENTS_PER_TYPE,
                device.getHeartRateMeasurements().size());
        assertTrue(device.getHeartRateMeasurements().stream().anyMatch(m -> m.getBpm() == 200));
        assertFalse(device.getHeartRateMeasurements().stream().anyMatch(m -> m.getBpm() == 70));
    }
}