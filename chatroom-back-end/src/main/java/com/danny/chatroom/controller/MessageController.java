package com.danny.chatroom.controller;

import com.danny.chatroom.annotation.CurrentUser;
import com.danny.chatroom.dto.response.ChatMessageResponse;
import com.danny.chatroom.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long roomId,
                                                                 @CurrentUser Long userId) {
        List<ChatMessageResponse> messages = messageService.getMessages(roomId, userId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{roomId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long roomId,
                                           @PathVariable Long messageId,
                                           @CurrentUser Long userId) {
        messageService.deleteMessage(roomId, messageId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roomId}/messages/{messageId}")
    public ResponseEntity<?> restoreDeletedMessage(@PathVariable Long roomId,
                                                   @PathVariable Long messageId,
                                                   @CurrentUser Long userId) {
        messageService.restoreDeletedMessage(roomId, messageId, userId);
        return ResponseEntity.noContent().build();
    }
}
