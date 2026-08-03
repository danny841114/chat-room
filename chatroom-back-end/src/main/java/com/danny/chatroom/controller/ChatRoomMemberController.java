package com.danny.chatroom.controller;

import com.danny.chatroom.annotation.CurrentUser;
import com.danny.chatroom.dto.request.AddMemberRequest;
import com.danny.chatroom.dto.response.ChatRoomMemberResponse;
import com.danny.chatroom.dto.response.UserInfo;
import com.danny.chatroom.service.ChatRoomMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomMemberController {
    private final ChatRoomMemberService chatRoomMemberService;

    @GetMapping("/available-users")
    public ResponseEntity<List<UserInfo>> getUsersExceptMe(@CurrentUser Long userId) {
        List<UserInfo> users = chatRoomMemberService.getUsersExceptMe(userId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{roomId}/available-users")
    public ResponseEntity<List<UserInfo>> getUsersExceptExisting(@PathVariable Long roomId,
                                                                 @CurrentUser Long userId) {
        List<UserInfo> users = chatRoomMemberService.getUsersNotInChatRooms(roomId, userId);
        return ResponseEntity.ok(users);
    }


    @PostMapping("/{roomId}/users")
    public ResponseEntity<List<ChatRoomMemberResponse>> addChatRoomUsers(@PathVariable Long roomId,
                                                                         @RequestBody AddMemberRequest request,
                                                                         @CurrentUser Long userId) {
        List<ChatRoomMemberResponse> responses = chatRoomMemberService.addChatRoomUsers(roomId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @DeleteMapping("/{roomId}/users/{deleteUserId}")
    public ResponseEntity<Void> deleteChatRoomUser(@PathVariable Long roomId,
                                                   @PathVariable Long deleteUserId,
                                                   @CurrentUser Long userId) {
        chatRoomMemberService.deleteChatRoomUser(roomId, deleteUserId, userId);
        return ResponseEntity.noContent().build();
    }
}
