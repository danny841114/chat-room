package com.danny.chatroom.controller;

import com.danny.chatroom.annotation.CurrentUser;
import com.danny.chatroom.dto.request.AddChatRoomRequest;
import com.danny.chatroom.dto.request.AddMemberRequest;
import com.danny.chatroom.dto.response.*;
import com.danny.chatroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getRooms(@CurrentUser Long userId) {
        List<ChatRoomResponse> rooms = chatService.getMyRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable Long roomId,
                                                    @CurrentUser Long userId) {
        ChatRoomResponse room = chatService.getRoom(roomId, userId);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long roomId,
                                                                 @CurrentUser Long userId) {
        List<ChatMessageResponse> messages = chatService.getMessages(roomId, userId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{roomId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long roomId,
                                           @PathVariable Long messageId,
                                           @CurrentUser Long userId) {
        chatService.deleteMessage(roomId, messageId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody AddChatRoomRequest request,
                                                           @CurrentUser Long userId) {
        ChatRoomResponse room = chatService.createChatRoom(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping("/available-users")
    public ResponseEntity<List<UserInfo>> getUsersExceptMe(@CurrentUser Long userId) {
        List<UserInfo> users = chatService.getUsersExceptMe(userId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{roomId}/available-users")
    public ResponseEntity<List<UserInfo>> getUsersExceptExisting(@PathVariable Long roomId,
                                                                     @CurrentUser Long userId) {
        List<UserInfo> users = chatService.getUsersNotInChatRooms(roomId, userId);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{roomId}/users/{deleteUserId}")
    public ResponseEntity<Void> deleteChatRoomUser(@PathVariable Long roomId,
                                                   @PathVariable Long deleteUserId,
                                                   @CurrentUser Long userId) {
        chatService.deleteChatRoomUser(roomId, deleteUserId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/users")
    public ResponseEntity<List<ChatRoomMemberResponse>> addChatRoomUsers(@PathVariable Long roomId,
                                                                         @RequestBody AddMemberRequest request,
                                                                         @CurrentUser Long userId) {
        List<ChatRoomMemberResponse> responses = chatService.addChatRoomUsers(roomId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}