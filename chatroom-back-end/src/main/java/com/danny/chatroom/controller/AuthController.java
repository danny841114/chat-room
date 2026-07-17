package com.danny.chatroom.controller;

import com.danny.chatroom.dto.UserDetailsImpl;
import com.danny.chatroom.dto.request.LoginRequest;
import com.danny.chatroom.dto.response.LoginResponse;
import com.danny.chatroom.dto.response.UserInfo;
import com.danny.chatroom.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.danny.chatroom.dto.response.UserInfo.fromUserDetails;
import static com.danny.chatroom.util.CookieUtil.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserInfo> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);

        UserDetailsImpl userDetails = response.getUserDetailsImpl();
        ResponseCookie cookie = createJwtCookie(response.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(fromUserDetails(userDetails));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = removeJwtCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/fetchMe")
    public ResponseEntity<UserInfo> fetchMe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) return ResponseEntity.ok(new UserInfo());
        return ResponseEntity.ok(fromUserDetails(userDetails));
    }
}