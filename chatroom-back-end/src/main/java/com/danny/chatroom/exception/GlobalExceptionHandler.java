package com.danny.chatroom.exception;

import com.danny.chatroom.dto.response.CustomErrorResponse;
import com.danny.chatroom.exception.custom.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<CustomErrorResponse> handleInternalServerException(InternalServerException ex) {
        log.error("Interval server error: ", ex);

        CustomErrorResponse error = CustomErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code("SYSTEM_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(error);
    }
}
