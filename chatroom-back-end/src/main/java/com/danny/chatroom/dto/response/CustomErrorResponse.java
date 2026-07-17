package com.danny.chatroom.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {
    private Integer status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
}
