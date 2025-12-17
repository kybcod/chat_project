package hello.chatting.chatroom.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupChatRoomReqDto {
    private Long roomId;
    private String userId;
    private String roomName;
    private Boolean active;

    @NotEmpty(message = "참여자 목록이 비어있습니다.")
    private List<String> userIds;
}
