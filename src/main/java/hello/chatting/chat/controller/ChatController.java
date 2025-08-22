package hello.chatting.chat.controller;

import hello.chatting.chat.ChatMessage;
import hello.chatting.chatroom.dto.ChatRoom;
import hello.chatting.chatroom.mapper.ChatRoomMapper;
import hello.chatting.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomMapper chatRoomMapper;
    private final ChatRoomService chatRoomService;

    @MessageMapping("chat/message")
    public void message(ChatMessage message) {

        if (message.getRoomId() == null || message.getRoomId().isEmpty()) {
            String newRoomId = UUID.randomUUID().toString();
            message.setRoomId(newRoomId);

            ChatRoom chatRoom = ChatRoom.builder().roomId(message.getRoomId()).build();
            chatRoomService.createChatRoom(chatRoom);
        }

        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
