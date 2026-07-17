package com.danny.chatroom.dto.response;

import com.danny.chatroom.dto.UserDetailsImpl;
import com.danny.chatroom.entity.ChatRoomMember;
import com.danny.chatroom.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long userId;
    private String account;
    private String username;

    public static UserInfo fromUserDetails(UserDetailsImpl userDetails) {
        return UserInfo.builder()
                .userId(userDetails.getId())
                .username(userDetails.getUsername())
                .account(userDetails.getAccount())
                .build();
    }

    public static UserInfo fromEntity(User user) {
        return UserInfo.builder()
                .userId(user.getId())
                .account(user.getAccount())
                .username(user.getUsername())
                .build();
    }

    public static UserInfo fromEntity(ChatRoomMember member) {
        return UserInfo.builder()
                .userId(member.getUser().getId())
                .username(member.getUser().getUsername())
                .account(member.getUser().getAccount())
                .build();
    }
}