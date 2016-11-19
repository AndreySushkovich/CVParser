package org.ansu.cvparser.exception;

/**
 * Author: Andrii Sushkovych
 * Date: 11/19/16
 */
public class ConversionException extends Exception {
    public ConversionException() {
        super();
    }
    public ConversionException(String message) {
        super(message);
    }
    public ConversionException(Throwable cause) {
        super(cause);
    }
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
