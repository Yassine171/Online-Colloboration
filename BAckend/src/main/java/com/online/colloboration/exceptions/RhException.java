package com.online.colloboration.exceptions;


public class RhException extends RuntimeException {
    public RhException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public RhException(String exMessage) {
        super(exMessage);
    }
}