package com.danny.chatroom.dto.response;

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
    public static class User{
        private Long id;
        private String username;
        private String account;
    }
}