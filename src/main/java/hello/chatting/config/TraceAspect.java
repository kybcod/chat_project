package hello.chatting.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TraceAspect {

    // Repository 패키지 경로 정확히 넣어줘!
    @Before("execution(* hello.chatting..service..*(..))")
    public void logService(JoinPoint joinPoint) {
        log.info(">>> service 호출: {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }
}
