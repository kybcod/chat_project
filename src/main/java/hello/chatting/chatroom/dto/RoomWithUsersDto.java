package hello.chatting.chatroom.dto;

import java.util.List;

public record RoomWithUsersDto(
        Long roomId,
        String roomName,
        String type,
        List<UserInfo> users
) {
    public static record UserInfo(
            String userId,
            String name,
            String email,
            String profileImage
    ) {}
}

