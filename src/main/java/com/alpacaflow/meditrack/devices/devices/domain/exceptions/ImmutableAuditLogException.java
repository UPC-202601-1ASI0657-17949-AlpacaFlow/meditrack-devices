package com.alpacaflow.meditrack.devices.devices.domain.exceptions;

/**
 * Thrown when a persisted clinical audit log is updated or deleted (TS10 immutability).
 */
public class ImmutableAuditLogException extends RuntimeException {

    public ImmutableAuditLogException() {
        super("Clinical audit logs are immutable and cannot be updated or deleted");
    }
}
