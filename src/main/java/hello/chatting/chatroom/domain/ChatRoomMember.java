package hello.chatting.chatroom.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_member")
@Builder
@Getter
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

    @Column(length = 20)
    @Builder.Default
    private String role = "MEMBER";

    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }
}
