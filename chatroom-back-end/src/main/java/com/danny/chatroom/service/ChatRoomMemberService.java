package com.danny.chatroom.service;

import com.danny.chatroom.dto.request.AddMemberRequest;
import com.danny.chatroom.dto.response.ChatRoomMemberResponse;
import com.danny.chatroom.dto.response.UserInfo;
import com.danny.chatroom.entity.ChatRoom;
import com.danny.chatroom.entity.ChatRoomMember;
import com.danny.chatroom.entity.User;
import com.danny.chatroom.repository.ChatRoomMemberRepository;
import com.danny.chatroom.repository.ChatRoomRepository;
import com.danny.chatroom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatRoomMemberService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public List<ChatRoomMember> findByChatRoomId(Long roomId) {
        return chatRoomMemberRepository.findByChatRoomId(roomId);
    }

    public List<UserInfo> getUsersExceptMe(Long userId) {
        List<User> otherUsers = userRepository.findAllByIdNot(userId);

        return otherUsers.stream()
                .map(UserInfo::fromEntity)
                .toList();
    }

    public List<UserInfo> getUsersNotInChatRooms(Long roomId, Long userId) {
        checkRoomMember(roomId, userId);

        List<User> users = userRepository.findUsersNotInChatRoom(roomId);

        return users.stream()
                .map(UserInfo::fromEntity)
                .toList();
    }

    @Transactional
    public List<ChatRoomMemberResponse> addChatRoomUsers(Long roomId, AddMemberRequest request, Long userId) {
        checkRoomMember(roomId, userId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> (new EntityNotFoundException("Chat room not found")));

        List<ChatRoomMemberResponse> responses = new ArrayList<>();
        List<Long> userIds = request.getUserIds();
        for (Long newUserId : userIds) {
            User user = userRepository.findById(newUserId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            LocalDateTime now = LocalDateTime.now();

            ChatRoomMember chatRoomMember = new ChatRoomMember();
            chatRoomMember.setChatRoom(chatRoom);
            chatRoomMember.setUser(user);
            chatRoomMember.setJoinedAt(now);
            chatRoomMember.setLastReadAt(now);
            chatRoomMemberRepository.save(chatRoomMember);

            ChatRoomMemberResponse response = ChatRoomMemberResponse.builder()
                    .chatRoomId(roomId)
                    .userId(newUserId)
                    .joinedAt(now)
                    .lastReadAt(now)
                    .build();
            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public void deleteChatRoomUser(Long roomId, Long deleteUserId, Long userId) {
        checkRoomMember(roomId, userId);
        checkRoomMember(roomId, deleteUserId);

        chatRoomMemberRepository.deleteByChatRoomIdAndUserId(roomId, deleteUserId);
    }

    private void checkRoomMember(Long roomId, Long userId) {
        Boolean isMember = chatRoomMemberRepository.existsByChatRoomIdAndUserId(roomId, userId);

        if (!isMember) throw new AccessDeniedException("Not chat room member, operation failed");
    }
}
