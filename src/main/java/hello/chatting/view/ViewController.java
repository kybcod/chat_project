package hello.chatting.view;

import hello.chatting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UserService userService;

    @GetMapping("/")
    public String main() {return "main";}

    @GetMapping("/chatting")
    public String chatting() {
        return "chat/chatting";
    }

    @GetMapping("/chatbot")
    public String chatbot() {
        return "chat/chatbot";
    }


    @GetMapping("/userList")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/userList";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

}

