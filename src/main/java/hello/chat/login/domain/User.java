package hello.chat.login.domain;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String role;
}
