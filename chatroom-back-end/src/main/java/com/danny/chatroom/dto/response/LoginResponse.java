package com.danny.chatroom.dto.response;

import com.danny.chatroom.dto.UserDetailsImpl;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UserDetailsImpl userDetailsImpl;
    private String token;
}
