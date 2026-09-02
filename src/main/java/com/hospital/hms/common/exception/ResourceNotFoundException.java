package com.hospital.hms.common.exception;

/**
 * Thrown when a lookup by id (or other unique key) finds nothing.
 * Mapped to HTTP 404 in {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forEntity(String entityName, Object id) {
        return new ResourceNotFoundException(entityName + " not found with id: " + id);
    }
}
