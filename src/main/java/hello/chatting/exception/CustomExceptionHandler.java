package hello.chatting.exception;

import hello.chatting.exception.Error.Error;
import hello.chatting.exception.dto.ExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class) // Exception 터지면 작동
    public ResponseEntity<?> apiException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new ExceptionResponseDTO<>(-1,e.getMessage(),null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("업로드 가능한 파일 크기를 초과했습니다. (최대 20MB)");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationApiException(MethodArgumentNotValidException e) {

        String msg = Error.ErrorMessage.getGroupedErrorMessage(e.getBindingResult().getFieldErrors());

        return new ResponseEntity<>(new ExceptionResponseDTO<>(-1, msg, null), HttpStatus.BAD_REQUEST);
    }


}
