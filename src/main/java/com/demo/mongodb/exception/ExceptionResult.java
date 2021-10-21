package com.demo.mongodb.exception;

public class ExceptionResult {
    public static final int RESULT_FAIL = 500;

    /*异常返回代码*/
    private Integer code;

    /*异常返回消息*/
    private String message;

    /*异常返回对象*/
    private Object data;

    public ExceptionResult(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
