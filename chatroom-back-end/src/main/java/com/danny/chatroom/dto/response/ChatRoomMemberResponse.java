package com.danny.chatroom.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMemberResponse {
    private Long chatRoomId;
    private Long userId;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;
}