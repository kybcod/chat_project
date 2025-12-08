package hello.chatting.user.controller;

import hello.chatting.user.dto.UserDto;
import hello.chatting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> userList() throws Exception {
        List<UserDto> userList = userService.findAll()
                .stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userList);
    }
}
