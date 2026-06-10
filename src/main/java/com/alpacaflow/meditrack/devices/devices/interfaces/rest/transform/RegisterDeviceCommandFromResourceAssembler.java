package com.alpacaflow.meditrack.devices.devices.interfaces.rest.transform;

import com.alpacaflow.meditrack.devices.devices.domain.model.commands.RegisterDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources.RegisterDeviceResource;

public class RegisterDeviceCommandFromResourceAssembler {
    public static RegisterDeviceCommand toCommandFromResource(RegisterDeviceResource resource) {
        return new RegisterDeviceCommand(resource.deviceId(), resource.holderId(), resource.model());
    }
}
