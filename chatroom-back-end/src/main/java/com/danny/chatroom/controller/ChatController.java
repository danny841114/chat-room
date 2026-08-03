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
        List<ChatRoomResponse> rooms = chatService.getRoomsByUserId(userId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable Long roomId,
                                                    @CurrentUser Long userId) {
        ChatRoomResponse room = chatService.getRoomById(roomId, userId);
        return ResponseEntity.ok(room);
    }

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody AddChatRoomRequest request,
                                                           @CurrentUser Long userId) {
        ChatRoomResponse room = chatService.createChatRoom(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

}