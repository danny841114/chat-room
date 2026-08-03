package com.danny.chatroom.service;

import com.danny.chatroom.dto.request.SendMessageRequest;
import com.danny.chatroom.dto.response.ChatMessageResponse;
import com.danny.chatroom.entity.ChatMessage;
import com.danny.chatroom.entity.ChatRoom;
import com.danny.chatroom.entity.ChatRoomMember;
import com.danny.chatroom.entity.User;
import com.danny.chatroom.repository.ChatMessageRepository;
import com.danny.chatroom.repository.ChatRoomMemberRepository;
import com.danny.chatroom.repository.ChatRoomRepository;
import com.danny.chatroom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MessageService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;


    @Transactional
    public List<ChatMessageResponse> getMessages(Long roomId, Long userId) {
        checkRoomMember(roomId, userId);

        // update last read time
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomIdAndUserId(roomId, userId);
        chatRoomMember.setLastReadAt(LocalDateTime.now());
        chatRoomMemberRepository.save(chatRoomMember);

        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(ChatMessageResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteMessage(Long roomId, Long messageId, Long userId) {
        checkRoomMember(roomId, userId);

        checkMessageOwner(messageId, userId);

        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        chatMessage.setDeletedAt(LocalDateTime.now());

        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void restoreDeletedMessage(Long roomId, Long messageId, Long userId) {
        checkRoomMember(roomId, userId);

        checkMessageOwner(messageId, userId);

        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        chatMessage.setDeletedAt(null);

        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long roomId, SendMessageRequest request) {
        Long senderId = request.getSenderId();

        checkRoomMember(roomId, senderId);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        room.setLastMessageTime(now);
        chatRoomRepository.save(room);

        ChatMessage message = new ChatMessage();

        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setMessageType("TEXT");
        message.setCreatedAt(now);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return ChatMessageResponse.fromEntity(savedMessage);
    }

    private void checkRoomMember(Long roomId, Long userId) {
        Boolean isMember = chatRoomMemberRepository.existsByChatRoomIdAndUserId(roomId, userId);
        if (!isMember) throw new AccessDeniedException("Not chat room member, operation failed");
    }

    private void checkMessageOwner(Long messageId, Long userId) {
        Boolean isMessageOwner = chatMessageRepository.existsByIdAndSenderId(messageId, userId);
        if (!isMessageOwner) throw new AccessDeniedException("User not message owner, operation failed");
    }
}
