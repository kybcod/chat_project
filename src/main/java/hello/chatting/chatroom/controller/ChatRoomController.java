package hello.chatting.chatroom.controller;

import hello.chatting.chatroom.dto.ChatRoom;
import hello.chatting.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 모든 채팅방 목록 반환
    @GetMapping("/list")
    public List<ChatRoom> room() {
        List<ChatRoom> list = chatRoomService.findAllRoom();
        return list;
    }


    // 특정 채팅방 조회
    @GetMapping("/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomById(roomId);
    }

    @PostMapping("create")
    public ResponseEntity<?> createRoom(@RequestBody ChatRoom chatRoom) {

        if (chatRoom.getRoomId() == null || chatRoom.getRoomId().isEmpty()) {
            String newRoomId = UUID.randomUUID().toString();
            //chatRoom.setRoomId(newRoomId);

            chatRoomService.createChatRoom(chatRoom);
        }

        return ResponseEntity.ok(chatRoom);
    }
}