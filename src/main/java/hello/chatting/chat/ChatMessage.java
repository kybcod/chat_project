package hello.chatting.chat;

import lombok.Data;

@Data
public class ChatMessage
{

    public enum MessageType {
        ENTER, TALK, EXIT, MATCH, MATCH_REQUEST;
    }
    private MessageType type;
    private String id;
    private String roomId;
    private String sender;
    private String message;
}
