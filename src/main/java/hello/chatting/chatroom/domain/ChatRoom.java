package hello.chatting.chatroom.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "chat_room")
@ToString
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             // BIGINT PK

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType type;         // PRIVATE / GROUP

    private String roomName;     // 그룹방 이름, 1:1은 null

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

}
