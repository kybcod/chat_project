package hello.chatting.user.dto;

import hello.chatting.user.domain.User;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String role;
    private String profileImage;

    // Entity → DTO 변환 (화면)
    public static UserDto toDto(User user) {
        String profileImage = user.getProfileImage();
        if (profileImage == null || profileImage.isEmpty()) {
            profileImage = "/images/orgProfile.png";
        }

        return UserDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .profileImage(profileImage)
                .build();
    }

}
