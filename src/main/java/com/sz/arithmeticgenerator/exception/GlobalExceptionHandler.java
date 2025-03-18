package com.sz.arithmeticgenerator.exception;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zyh
 * @version 1.0.0
 * @date 2025/03/18
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param exception 异常
     * @author zyh
     * @date 2025/03/18
     */
    @ExceptionHandler(value = Exception.class)
    public void allException(Exception exception) {
        // 返回错误结果
         System.out.println(exception.getMessage());
    }
}

