package com.demo.mongodb;

import com.demo.mongodb.exception.ExceptionResult;
import com.demo.mongodb.exception.ExceptionService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类
 */
@ControllerAdvice()
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ExceptionService.class)
    public ExceptionResult handleException(ExceptionService ex) {
        ExceptionResult result = new ExceptionResult(ExceptionResult.RESULT_FAIL, ex.getMessage(), "");
        return result;
    }
}
