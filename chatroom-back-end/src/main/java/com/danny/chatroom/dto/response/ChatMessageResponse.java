package com.danny.chatroom.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String senderAccount;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}