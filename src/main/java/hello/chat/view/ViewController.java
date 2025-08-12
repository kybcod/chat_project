package hello.chat.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ViewController {


    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/chatting")
    public String chatting() {
        return "chat/chatting :: content";
    }

    @GetMapping("/chatbot")
    public String chatbot() {
        return "chat/chatbot :: content";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

}

