package hello.chatting.chatroom.controller;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.dto.ChatRoomDto;
import hello.chatting.chatroom.dto.ChatRoomReqDto;
import hello.chatting.chatroom.service.ChatRoomService;
import jakarta.validation.Valid;
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
    public List<ChatRoomDto> findAllByUserId(ChatRoomReqDto dto) {
        return chatRoomService.findAllByUserId(dto.getUserId()).stream()
                .map(ChatRoomDto::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("find")
    public ChatRoomDto findRoom(@Valid @RequestBody ChatRoomReqDto dto) throws Exception {
        ChatRoom privateRoom = chatRoomService.findPrivateRoom(dto);
        return ChatRoomDto.toDto(privateRoom);
    }

    @PostMapping("create")
    public ChatRoomDto createRoom(@Valid @RequestBody ChatRoomReqDto dto) throws Exception {
        ChatRoom room = chatRoomService.createPrivateRoom(dto);
        return ChatRoomDto.toDto(room);
    }

}