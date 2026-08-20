package com.danny.chatroom.service;

import com.danny.chatroom.entity.User;
import com.danny.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends OidcUserService {
    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");

        userRepository.findByAccount(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setAccount(email);
            newUser.setUsername(name);
            newUser.setPassword("test"); // 要修改
            return userRepository.save(newUser);
        });

        return oidcUser;
    }
}
