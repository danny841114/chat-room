package com.danny.chatroom.dto.response;

import com.danny.chatroom.entity.ChatRoom;
import com.danny.chatroom.entity.ChatRoomMember;
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
    private List<User> users;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Long id;
        private String username;
        private String account;
    }

    public static ChatRoomResponse.User fromEntity(ChatRoomMember member) {
        return ChatRoomResponse.User.builder()
                .id(member.getUser().getId())
                .username(member.getUser().getUsername())
                .account(member.getUser().getAccount())
                .build();
    }

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