package com.hospital.hms.common.exception;

/**
 * Thrown when an operation would violate a business-level uniqueness rule
 * (e.g. creating a patient with an email that already exists).
 * Mapped to HTTP 409 in {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
