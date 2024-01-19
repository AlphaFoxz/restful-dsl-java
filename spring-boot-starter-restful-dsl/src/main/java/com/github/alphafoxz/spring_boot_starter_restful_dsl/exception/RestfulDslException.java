package com.github.alphafoxz.spring_boot_starter_restful_dsl.exception;

import cn.hutool.core.map.MapUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class RestfulDslException extends RuntimeException {
    private ResponseEntity<?> responseEntity;

    public RestfulDslException(String msg, HttpStatus httpStatus) {
        super(msg);
        Map<String, Object> body = MapUtil.newHashMap(4);
        body.put("message", msg);
        body.put("code", httpStatus.value());
        this.responseEntity = new ResponseEntity<>(body, httpStatus);
    }

    public RestfulDslException(String msg, HttpStatus httpStatus, Throwable throwable) {
        super(msg, throwable);
        if (msg != null && throwable != null) {
            msg += throwable.getMessage();
        }
        Map<String, Object> body = MapUtil.newHashMap(4);
        body.put("message", msg);
        body.put("code", httpStatus.value());
        this.responseEntity = new ResponseEntity<>(body, httpStatus);
    }
}
