package hello.chatting.login.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User extends DefaultOAuth2User {
    private final String loginId;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String loginId) {
        super(authorities, attributes, nameAttributeKey);
        this.loginId = loginId;
    }

    @Override
    public String getName() {
        return loginId;
    }
}
