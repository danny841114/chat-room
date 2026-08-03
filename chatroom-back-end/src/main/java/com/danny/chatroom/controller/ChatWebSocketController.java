package com.danny.chatroom.controller;

import com.danny.chatroom.dto.response.ChatMessageResponse;
import com.danny.chatroom.dto.response.ChatRoomResponse;
import com.danny.chatroom.dto.request.SendMessageRequest;
import com.danny.chatroom.entity.ChatRoomMember;
import com.danny.chatroom.service.ChatRoomMemberService;
import com.danny.chatroom.service.ChatRoomService;
import com.danny.chatroom.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatWebSocketController {
    private final ChatRoomService chatRoomService;
    private final ChatRoomMemberService chatRoomMemberService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, SendMessageRequest request) {
        ChatMessageResponse savedMessage = messageService.sendMessage(roomId, request);
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, savedMessage);

        // 有在此間聊天室的使用者都需要被推播新的聊天室清單(排序改變)
        List<Long> userIds = chatRoomMemberService.findUserIdsByChatRoomId(roomId);
        for(Long userId: userIds){
            List<ChatRoomResponse> myRooms = chatRoomService.getRoomsByUserId(userId);
            messagingTemplate.convertAndSend("/topic/users/" + userId + "/rooms", myRooms);
        }
    }
}
