package hello.chat.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
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
        return "fragments/chatting :: content";
    }

    @GetMapping("/chatbot")
    public String chatbot() {
        return "fragments/chatbot :: content";
    }

}

