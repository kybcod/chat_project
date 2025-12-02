package hello.chatting.chatroom.controller;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.dto.ChatRoomDto;
import hello.chatting.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/list")
    public List<ChatRoomDto> findAllByUserId(String userId) {
        return chatRoomService.findAllByUserId(userId).stream()
                .map(ChatRoomDto::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("find")
    public ChatRoom findRoom(String me, String friendId) {
        return chatRoomService.findPrivateRoom(me, friendId);
    }

    @PostMapping("create")
    public ChatRoomDto createRoom(String me, String friend) {
        return chatRoomService.createPrivateRoom(me, friend);
    }

}