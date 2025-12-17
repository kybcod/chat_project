package hello.chatting.chatroom.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_member")
@Builder(toBuilder = true)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;         // ChatRoom.id FK

    @Column(nullable = false)
    private String userId;

    @Column
    @Builder.Default
    private Boolean active = true;

    @Column(length = 20)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.MEMBER;

    @Column(insertable = false, updatable = false)
    private LocalDateTime joinedAt;

}
