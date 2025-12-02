package hello.chatting.chat.controller;

import hello.chatting.chat.domain.ChatMessage;
import hello.chatting.chat.repository.ChatRepository;
import hello.chatting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("chat/message")
    public void message(ChatMessage message) {

        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(message.getRoomId())
                .sender(message.getSender())
                .message(message.getMessage())
                .build();
        chatService.save(chatMessage);

        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @GetMapping("/chat/messages/{roomId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable Long roomId) {
        return chatService.findByRoomIdOrderByCreatedAt(roomId);
    }

}
