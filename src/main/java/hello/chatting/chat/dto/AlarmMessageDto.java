package hello.chatting.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMessageDto {
    private String receiver; // 알림 받을 사람
    private String content;  // 알림 내용
}
