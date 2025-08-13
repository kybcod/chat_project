package hello.chat.login.mapper;

import hello.chat.login.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {

    User findUserByLoginId(String loginId);
    void save(User user);
}
