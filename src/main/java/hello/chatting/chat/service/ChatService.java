package hello.chatting.chat.service;

import hello.chatting.chat.domain.ChatMessage;
import hello.chatting.chat.dto.ChatMessageDto;
import hello.chatting.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional
    public void save(ChatMessage chatMessage) {
        chatRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> findByRoomIdOrderByCreatedAt(Long roomId) {
        return chatRepository.findByRoomIdOrderByCreatedAt(roomId);
    }
}
