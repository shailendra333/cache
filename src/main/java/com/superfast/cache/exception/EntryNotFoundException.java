package com.superfast.cache.exception;

/**
 * Entry not found exception.
 *
 * @author Seshu Pasam
 */

public class EntryNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -8613648319429506805L;

    public EntryNotFoundException() {
        super();
    }

    public EntryNotFoundException(String message) {
        super(message);
    }
}
