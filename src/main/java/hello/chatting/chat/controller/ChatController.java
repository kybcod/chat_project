package hello.chatting.chat.controller;

import hello.chatting.chat.domain.ChatMessage;
import hello.chatting.chat.dto.AlarmMessageDto;
import hello.chatting.chat.dto.ChatMessageDto;
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
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/alarm")
    public void sendAlarm(AlarmMessageDto alarmMessageDto) {
        messagingTemplate.convertAndSendToUser(alarmMessageDto.getReceiver(),
                "/queue/alarm",
                        alarmMessageDto);
    }

    @MessageMapping("chat/message")
    public void message(ChatMessageDto message) {
        ChatMessage entity = ChatMessageDto.toEntity(message);
        chatService.save(entity);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @GetMapping("/chat/messages/{roomId}")
    @ResponseBody
    public List<ChatMessageDto> getMessages(@PathVariable Long roomId) {
        return chatService.findByRoomIdOrderByCreatedAt(roomId)
                .stream()
                .map(ChatMessageDto::toDto)
                .collect(Collectors.toList());
    }

}
