package com.auriga_tt.service;

import com.auriga_tt.model.CustomOAuth2User;
import com.auriga_tt.model.User;
import com.auriga_tt.model.UserContactMethod;
import com.auriga_tt.repository.UserContactMethodRepository;
import com.auriga_tt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserContactMethodRepository contactMethodRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String provider = userRequest.getClientRegistration().getRegistrationId();

        Optional<UserContactMethod> existingContact = contactMethodRepository
                .findByContactTypeAndContactValue(UserContactMethod.ContactType.EMAIL, email);

        User user;
        if (existingContact.isPresent()) {
            user = existingContact.get().getUser();
            // Link the OAuth2 provider to the existing account
            user.setOAuth2Provider(provider);
            userRepository.save(user);
        } else {
            // Create new user
            user = new User();
            user.setUsername(email.split("@")[0] + "_" + provider);
            user.setFullName(name);
            user.setPasswordHash(""); // OAuth2 users don't need password
            user.setRole("ROLE_PLAYER"); // Default role
            user.setIsActive(User.UserStatus.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user = userRepository.save(user);

            // Create verified email contact
            UserContactMethod emailContact = new UserContactMethod();
            emailContact.setUser(user);
            emailContact.setContactType(UserContactMethod.ContactType.EMAIL);
            emailContact.setContactValue(email);
            emailContact.setIsPrimary(true);
            emailContact.setIsVerified(true);
            emailContact.setCreatedAt(LocalDateTime.now());
            contactMethodRepository.save(emailContact);
        }

        return new CustomOAuth2User(oauth2User, user);
    }
}