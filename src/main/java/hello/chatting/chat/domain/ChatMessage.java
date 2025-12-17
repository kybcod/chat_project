package hello.chatting.chat.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private String sender;

    private String message;
    private String type;
    private String fileUrl;
    private String fileName;
    private String fileType;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
