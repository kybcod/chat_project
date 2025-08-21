package hello.chatting.chatroom.controller;

import hello.chatting.chatroom.mapper.ChatRoomMapper;
import hello.chatting.chatroom.dto.ChatRoom;
import hello.chatting.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        List<ChatRoom> list = chatRoomService.findAllRoom();
        return list;
    }


    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom() {
        return chatRoomService.createChatRoom();
    }


    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomById(roomId);
    }
}