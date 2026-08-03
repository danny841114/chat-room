package com.danny.chatroom.service;

import com.danny.chatroom.dto.request.AddChatRoomRequest;
import com.danny.chatroom.dto.response.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public List<ChatRoomResponse> getRoomsByUserId(Long userId) {
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findRoomsByUserId(userId);
        if (chatRoomMembers.isEmpty()) return Collections.emptyList();

        List<Long> roomIds = chatRoomMembers.stream()
                .map(chatRoomMember -> chatRoomMember.getChatRoom().getId())
                .toList();

        // Map => key: roomId, value: unreadCount
        List<Object[]> unreadCountsRaw = chatRoomMemberRepository.countUnreadMessagesBatch(userId, roomIds);
        Map<Long, Long> unreadCountMap = unreadCountsRaw.stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        List<ChatRoomResponse> responses = new ArrayList<>();
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            ChatRoom room = chatRoomMember.getChatRoom();

            Long unreadCount = unreadCountMap.getOrDefault(room.getId(), 0L);

            String roomName = "PRIVATE".equals(room.getType())
                    ? getPrivateRoomName(userId, room)
                    : room.getName();

            ChatRoomResponse response = ChatRoomResponse.builder()
                    .roomId(room.getId())
                    .name(roomName)
                    .type(room.getType())
                    .unreadMessagesCount(unreadCount)
                    .users(null)
                    .build();

            responses.add(response);
        }

        return responses;
    }

    public ChatRoomResponse getRoomById(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));

        checkRoomMember(roomId, userId);

        Long count = chatRoomMemberRepository.countUnreadMessages(userId, roomId);

        List<UserInfo> users = chatRoom.getMembers()
                .stream()
                .map(UserInfo::fromEntity)
                .toList();

        String roomName = "PRIVATE".equals(chatRoom.getType())
                ? getPrivateRoomName(userId, chatRoom)
                : chatRoom.getName();

        return ChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .name(roomName)
                .type(chatRoom.getType())
                .unreadMessagesCount(count)
                .users(users)
                .build();
    }

    @Transactional
    public ChatRoomResponse createChatRoom(AddChatRoomRequest addChatRoomRequest, Long userId) {
        Set<Long> allMemberIds = new HashSet<>(addChatRoomRequest.getMemberIds());

        String type = addChatRoomRequest.getType();

        if ("PRIVATE".equals(type)) {
            allMemberIds.remove(userId); // avoid get current user ID

            Long targetUserId = allMemberIds.iterator().next(); // get next factor

            if (userId.equals(targetUserId))
                throw new IllegalArgumentException("Can not create a private chat with yourself");

            Optional<ChatRoom> existingRoom = chatRoomRepository.findPrivateRoomBetweenUsers(userId, targetUserId);

            if (existingRoom.isPresent()) return ChatRoomResponse.fromEntity(existingRoom.get());
        }

        LocalDateTime now = LocalDateTime.now();

        ChatRoom newChatRoom = new ChatRoom();

        newChatRoom.setName(addChatRoomRequest.getName());
        newChatRoom.setType(type);
        newChatRoom.setCreatedAt(now);
        newChatRoom.setLastMessageTime(now);

        ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);

        allMemberIds.add(userId);

        List<User> users = userRepository.findAllById(allMemberIds);
        if (users.size() != allMemberIds.size()) {
            throw new EntityNotFoundException("One or more users not found");
        }

        List<ChatRoomMember> chatRoomMembers = new ArrayList<>();
        for (User user : users) {
            ChatRoomMember chatRoomMember = new ChatRoomMember();

            chatRoomMember.setUser(user);
            chatRoomMember.setChatRoom(savedChatRoom);
            chatRoomMember.setJoinedAt(savedChatRoom.getCreatedAt());
            chatRoomMember.setLastReadAt(savedChatRoom.getCreatedAt());

            chatRoomMembers.add(chatRoomMember);
        }
        chatRoomMemberRepository.saveAll(chatRoomMembers);

        return ChatRoomResponse.fromEntity(savedChatRoom);
    }

    private void checkRoomMember(Long roomId, Long userId) {
        Boolean isMember = chatRoomMemberRepository.existsByChatRoomIdAndUserId(roomId, userId);

        if (!isMember) throw new AccessDeniedException("Not chat room member, operation failed");
    }

    private String getPrivateRoomName(Long userId, ChatRoom room) {
        return room.getMembers().stream()
                .map(ChatRoomMember::getUser)
                .filter(u -> !Objects.equals(u.getId(), userId))
                .findFirst()
                .map(User::getUsername)
                .orElse(null);
    }
}