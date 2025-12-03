package hello.chatting.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
public class CustomErrorController implements ErrorController{

	@RequestMapping("error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		log.error("요청 주소 : {}", request.getRequestURI());

		if(status != null) {
			int statusCode = Integer.valueOf(status.toString());

			log.error("오류 코드 : {}", statusCode);

			if(statusCode == HttpStatus.BAD_REQUEST.value()) {
				return "/errorPage/400page";
			}
			if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
				return "/errorPage/401page";
			}
			if(statusCode == HttpStatus.FORBIDDEN.value()) {
				return "/errorPage/403page";
			}
			if(statusCode == HttpStatus.NOT_FOUND.value()) {
				return "/errorPage/404page";
			}
			if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				return "/errorPage/500page";
			}
		}
		
		return "/errorPage/500page";
	}
}
