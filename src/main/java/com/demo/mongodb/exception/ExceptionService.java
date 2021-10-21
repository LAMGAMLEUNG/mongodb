package com.demo.mongodb.exception;

public class ExceptionService extends RuntimeException {
    public ExceptionService(String msg) {
        super(msg);
    }
}
