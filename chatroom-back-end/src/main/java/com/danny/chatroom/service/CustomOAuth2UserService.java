package com.danny.chatroom.service;

import com.danny.chatroom.entity.User;
import com.danny.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        System.err.println("執行到這1");

        userRepository.findByAccount(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setAccount(email);
            newUser.setUsername(name);
            newUser.setPassword("test"); // 要修改
            System.err.println("執行到這2");
            return userRepository.save(newUser);
        });

        System.err.println("執行到這3");

        return oAuth2User;
    }
}
