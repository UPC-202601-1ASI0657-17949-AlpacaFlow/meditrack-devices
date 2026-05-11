package com.alpacaflow.meditrack.devices.domain.model.commands;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateDeviceCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateDeviceCommandTest {

    @Test
    void shouldThrowExceptionWhenModelIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new CreateDeviceCommand("", 1L));
    }

    @Test
    void shouldThrowExceptionWhenHolderIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new CreateDeviceCommand("Watch", null));
    }
}