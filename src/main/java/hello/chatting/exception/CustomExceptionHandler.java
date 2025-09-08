package hello.chatting.exception;

import hello.chatting.exception.dto.ExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class) // Exception 터지면 작동
    public ResponseEntity<?> apiException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new ExceptionResponseDTO<>(-1,e.getMessage(),null), HttpStatus.BAD_REQUEST);
    }



}
