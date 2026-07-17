package com.danny.chatroom.dto.response;

import com.danny.chatroom.entity.ChatRoom;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomResponse {
    private Long roomId;
    private String name;
    private String type;
    private Long unreadMessagesCount;
    private List<UserInfo> users;

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .name(chatRoom.getName())
                .type(chatRoom.getType())
                .unreadMessagesCount(0L)
                .users(null)
                .build();
    }
}