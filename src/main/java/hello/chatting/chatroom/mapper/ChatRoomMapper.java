package hello.chatting.chatroom.mapper;

import hello.chatting.chatroom.dto.ChatRoom;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface ChatRoomMapper {
    List<ChatRoom> findAllRoom();
    ChatRoom createChatRoom();
    ChatRoom findRoomById(String roomId);
}
