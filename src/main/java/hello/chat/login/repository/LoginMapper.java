package hello.chat.login.repository;

import hello.chat.login.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {

    User findUserByLoginId(String name);
}
