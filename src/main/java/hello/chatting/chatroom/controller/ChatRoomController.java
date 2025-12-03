package hello.chatting.chatroom.controller;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import hello.chatting.chatroom.dto.ChatRoomDto;
import hello.chatting.chatroom.dto.ChatRoomMemberDto;
import hello.chatting.chatroom.dto.ChatRoomReqDto;
import hello.chatting.chatroom.service.ChatRoomService;
import hello.chatting.user.service.UserService;
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
    private final UserService userService;

    @GetMapping("/list")
    public List<ChatRoomDto> findAllByUserId(ChatRoomReqDto dto) {
        return chatRoomService.findAllByUserId(dto.getUserId()).stream()
                .map(chatRoom -> {
                    String friendName = userService.extractFriendName(
                            chatRoom.getRoomName(),
                            dto.getUserId()
                    );
                    return ChatRoomDto.toDto(chatRoom, friendName);
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/find")
    public ChatRoomDto findRoom(@Valid @RequestBody ChatRoomReqDto dto) throws Exception {
        ChatRoom privateRoom = chatRoomService.findPrivateRoom(dto);
        String friendName = chatRoomService.getFriendName(privateRoom, dto.getUserId());
        return ChatRoomDto.toDto(privateRoom, friendName);
    }

    @PostMapping("/create")
    public ChatRoomDto createRoom(@Valid @RequestBody ChatRoomReqDto dto) throws Exception {
        ChatRoom room = chatRoomService.createPrivateRoom(dto);
        String friendName = chatRoomService.getFriendName(room, dto.getUserId());
        return ChatRoomDto.toDto(room, friendName);
    }

    @GetMapping("/findRoom")
    public ChatRoomMemberDto getRoomInfo(ChatRoomReqDto dto) {
        ChatRoomMember userIdNot = chatRoomService.findByRoomIdAndUserIdNot(dto);
        return ChatRoomMemberDto.toDto(userIdNot);
    }
}