package hello.chatting.chatroom.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             // BIGINT PK

    @Column(nullable = false)
    private String type;         // PRIVATE / GROUP

    private String roomName;     // 그룹방 이름, 1:1은 null

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

}
