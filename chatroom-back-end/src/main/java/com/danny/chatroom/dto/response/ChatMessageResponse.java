package com.danny.chatroom.dto.response;

import com.danny.chatroom.entity.ChatMessage;
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

    public static ChatMessageResponse fromEntity(ChatMessage message) {
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername())
                .senderAccount(message.getSender().getAccount())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt())
                .deletedAt(message.getDeletedAt())
                .build();
    }
}