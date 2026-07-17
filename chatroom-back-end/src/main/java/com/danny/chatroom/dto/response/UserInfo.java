package com.danny.chatroom.dto.response;

import com.danny.chatroom.dto.UserDetailsImpl;
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
}