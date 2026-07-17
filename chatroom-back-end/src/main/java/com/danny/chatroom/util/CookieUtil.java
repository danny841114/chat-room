package com.danny.chatroom.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie createJwtCookie(String token) {
        return ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // 本機開發先 false，正式 HTTPS 改 true
                .path("/")
                .maxAge(86400)
                .sameSite("Lax")
                .build();
    }

    public static ResponseCookie removeJwtCookie() {
        return ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
