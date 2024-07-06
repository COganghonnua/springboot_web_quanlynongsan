package com.example.Buoi2.services;

import com.example.Buoi2.entity.User;
import com.example.Buoi2.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");
        String googleId = oauth2User.getName(); // Hoặc dùng thuộc tính khác nếu cần

        // Kiểm tra xem người dùng đã tồn tại trong DB chưa
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // Nếu người dùng chưa tồn tại, tạo mới
            user = new User();
            user.setEmail(email);
            user.setGoogleId(googleId);
            String name = oauth2User.getAttribute("name");
            if (name == null) {
                name = "Default Name"; // Thiết lập giá trị mặc định nếu name là null
            }
            user.setUsername(name); // Hoặc các thuộc tính khác
            user.setAddress("Default Address"); // Thiết lập giá trị mặc định
            user.setPassword("DefaultPassword"); // Thiết lập giá trị mặc định
            user.setName(name); // Thiết lập giá trị name
            userRepository.save(user);
        }
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                oauth2User.getAttributes(),
                "name");
    }
}
