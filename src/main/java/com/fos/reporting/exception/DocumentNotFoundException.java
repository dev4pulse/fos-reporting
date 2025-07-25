package com.fos.reporting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception that is thrown when a requested document cannot be found.
 * The @ResponseStatus annotation tells Spring to automatically return a 404 NOT FOUND
 * HTTP status code whenever this exception is thrown from a controller.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String message) {
        super(message);
    }

    public DocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}