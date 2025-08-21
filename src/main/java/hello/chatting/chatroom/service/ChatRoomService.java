package hello.chatting.chatroom.service;

import hello.chatting.chatroom.dto.ChatRoom;
import hello.chatting.chatroom.mapper.ChatRoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomMapper chatRoomMapper;


    public List<ChatRoom> findAllRoom() {
        return chatRoomMapper.findAllRoom();
    }

    public ChatRoom createChatRoom() {
        return chatRoomMapper.createChatRoom();
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomMapper.findRoomById(roomId);
    }
}
