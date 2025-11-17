package hello.chatting.user.mapper;

import hello.chatting.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User findUserByLoginId(String loginId);
    void save(User user);

    List<User> findAll();
}
