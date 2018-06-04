package com.superfast.cache.exception;

/**
 * Generic cache exception.
 *
 * @author Seshu Pasam
 */

public class CacheException extends RuntimeException {
    private static final long serialVersionUID = 5632237203461348136L;

    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable t) {
        super(t);
    }

    public CacheException(String message, Throwable t) {
        super(message, t);
    }
}
