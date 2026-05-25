package com.topleague.predict.infrastructure.config;

import com.topleague.predict.domain.exception.TopPredictErrorCode;
import com.topleague.predict.domain.exception.TopPredictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Map<TopPredictErrorCode, HttpStatus> errorStatusMap;

    public GlobalExceptionHandler(Map<TopPredictErrorCode, HttpStatus> errorStatusMap) {
        this.errorStatusMap = errorStatusMap;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleUnhandledException(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(TopPredictException.class)
    public ResponseEntity<Void> handleTopPredictException(TopPredictException e) {
        log.error("Domain exception [{}]: {}", e.getErrorCode().getCode(), e.getMessage(), e);
        HttpStatus status = errorStatusMap.getOrDefault(e.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(status).build();
    }
}
