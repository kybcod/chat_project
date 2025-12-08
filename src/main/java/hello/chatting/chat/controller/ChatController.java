package hello.chatting.chat.controller;

import hello.chatting.chat.domain.ChatMessage;
import hello.chatting.chat.dto.AlarmMessageDto;
import hello.chatting.chat.dto.ChatMessageDto;
import hello.chatting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @MessageMapping("chat/typing")
    public void typing(ChatMessageDto message) {
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @GetMapping("/chat/messages/{roomId}")
    @ResponseBody
    public ResponseEntity<?> getMessages(@PathVariable Long roomId) {
        List<ChatMessageDto> chatMessageDtoList = chatService.findByRoomIdOrderByCreatedAt(roomId)
                .stream()
                .map(ChatMessageDto::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatMessageDtoList);
    }

    @PostMapping("/chat/upload")
    @ResponseBody
    public ResponseEntity<?> upload(@RequestParam("chatFile") MultipartFile chatFile,
                                    @RequestParam("roomId") Long roomId,
                                    @RequestParam("sender") String sender) throws Exception {
        ChatMessage chatMessage = chatService.chatFileUpload(chatFile, roomId, sender);
        return ResponseEntity.ok(ChatMessageDto.toDto(chatMessage));
    }

}
