package com.alpacaflow.meditrack.devices.devices.domain.model.aggregates;


import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.entities.*;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EDeviceStatus;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.Holder;
import com.alpacaflow.meditrack.devices.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*** Device aggregate root
 * @summary
 * This aggregate root represents a medical device.
 * A device has a model, status, holder, and measurements.
 * @since 1.0
 */
@Getter
@Entity
public class Device extends AuditableAbstractAggregateRoot<Device> {
    public static final int MAX_RETAINED_MEASUREMENTS_PER_TYPE = 100;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EDeviceStatus status;

    @Embedded
    private Holder holder;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    private List<Measurement> measurements;

    public Device() {
        this.model = Strings.EMPTY;
        this.status = EDeviceStatus.ACTIVE;
        this.holder = new Holder();
        this.measurements = new ArrayList<>();
    }

    /**
     * Create a new device with the given model and holder
     * @param model The model of the device
     * @param holderId The holder ID
     */
    public Device(String model, Long holderId) {
        this();
        this.model = model;
        this.status = EDeviceStatus.ACTIVE;
        this.holder = new Holder(holderId, "SeniorCitizen");
    }

    /**
     * Create a new device with information from the command
     * @param command The command to create the device
     * @see CreateDeviceCommand
     */
    public Device(CreateDeviceCommand command) {
        this(command.model(), command.holderId());
    }

    /**
     * Assigns this device to a senior citizen when it is still unassigned.
     */
    public void assignHolder(Long holderId) {
        if (holderId == null || holderId <= 0) {
            throw new IllegalArgumentException("holderId must be a positive number");
        }
        Long currentHolderId = holder != null ? holder.holderId() : 0L;
        if (currentHolderId != null && currentHolderId > 0) {
            if (currentHolderId.equals(holderId)) {
                return;
            }
            throw new IllegalArgumentException(
                    "Device is already assigned to senior citizen %d".formatted(currentHolderId));
        }
        this.holder = new Holder(holderId, "SeniorCitizen");
    }

    /**
     * Add a heart rate measurement to the device
     * @param bpm The heart rate in beats per minute
     */
    public void addHeartRate(int bpm) {
        addHeartRateMeasurement(new HeartRateMeasurement(bpm));
    }

    public void addHeartRateMeasurement(HeartRateMeasurement measurement) {
        this.measurements.add(measurement);
    }

    /**
     * Add a temperature measurement to the device
     * @param celsius The temperature in celsius
     */
    public void addTemperature(double celsius) {
        addTemperatureMeasurement(new TemperatureMeasurement(celsius));
    }

    public void addTemperatureMeasurement(TemperatureMeasurement measurement) {
        this.measurements.add(measurement);
    }

    /**
     * Add an oxygen saturation measurement to the device
     * @param spo2 The oxygen saturation percentage
     */
    public void addOxygen(int spo2) {
        addOxygenMeasurement(new OxygenMeasurement(spo2));
    }

    public void addOxygenMeasurement(OxygenMeasurement measurement) {
        this.measurements.add(measurement);
    }

    /**
     * Add a blood pressure measurement to the device
     * @param diastolic The diastolic pressure
     * @param systolic The systolic pressure
     */
    public void addBloodPressure(int diastolic, int systolic) {
        var measurement = new BloodPressureMeasurement(diastolic, systolic);
        this.measurements.add(measurement);
    }

    /**
     * Get measurements of a specific type
     * @param type The class type of the measurement
     * @return List of measurements of the specified type
     */
    public <T extends Measurement> List<T> getMeasurementsOfType(Class<T> type) {
        return measurements.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    /**
     * Get heart rate measurements
     * @return List of heart rate measurements
     */
    public List<HeartRateMeasurement> getHeartRateMeasurements() {
        return getMeasurementsOfType(HeartRateMeasurement.class);
    }

    /**
     * Get temperature measurements
     * @return List of temperature measurements
     */
    public List<TemperatureMeasurement> getTemperatureMeasurements() {
        return getMeasurementsOfType(TemperatureMeasurement.class);
    }

    /**
     * Get oxygen saturation measurements
     * @return List of oxygen measurements
     */
    public List<OxygenMeasurement> getOxygenMeasurements() {
        return getMeasurementsOfType(OxygenMeasurement.class);
    }

    /**
     * Get blood pressure measurements
     * @return List of blood pressure measurements
     */
    public List<BloodPressureMeasurement> getBloodPressureMeasurements() {
        return getMeasurementsOfType(BloodPressureMeasurement.class);
    }

    /**
     * Check if a new measurement of the given type would exceed the retention window.
     */
    public <T extends Measurement> boolean exceedsMeasurementRetentionOfType(Class<T> type) {
        return countMeasurementsOfType(type) >= MAX_RETAINED_MEASUREMENTS_PER_TYPE;
    }

    /**
     * Remove the oldest measurement of a specific type (by measuredAt, then insertion order).
     */
    public <T extends Measurement> void removeOldestMeasurementOfType(Class<T> type) {
        measurements.stream()
                .filter(type::isInstance)
                .min(Comparator
                        .comparing(Measurement::getMeasuredAt)
                        .thenComparingInt(measurements::indexOf))
                .ifPresent(measurements::remove);
    }

    private <T extends Measurement> long countMeasurementsOfType(Class<T> type) {
        return measurements.stream()
                .filter(type::isInstance)
                .count();
    }
}



