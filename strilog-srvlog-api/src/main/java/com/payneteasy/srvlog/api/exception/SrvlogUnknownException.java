package com.payneteasy.srvlog.api.exception;

public class SrvlogUnknownException extends RuntimeException {

    private final int status;

    public SrvlogUnknownException(String message, int status) {
        super(message);
        this.status = status;
    }
}
