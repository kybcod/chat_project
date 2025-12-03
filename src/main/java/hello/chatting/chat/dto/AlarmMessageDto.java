package hello.chatting.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMessageDto {
    private String senderName; // 채팅 보낸 사람
    private String receiver; // 알림 받을 사람
    private String content;  // 알림 내용
    private String senderProfileImage;  // 채팅 보낸 사람 프로필 이미지
}
