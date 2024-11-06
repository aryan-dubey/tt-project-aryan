package com.auriga_tt.service;

import com.auriga_tt.model.User;
import com.auriga_tt.model.UserContactMethod;
import com.auriga_tt.repository.UserRepository;
import com.auriga_tt.repository.UserContactMethodRepository;
import com.auriga_tt.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserContactMethodRepository contactMethodRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Try to find user by username
        User user = userRepository.findByUsername(login)
                .orElseGet(() -> {
                    // If not found by username, try to find by email
                    Optional<UserContactMethod> contactMethod = contactMethodRepository
                            .findByContactTypeAndContactValue(UserContactMethod.ContactType.EMAIL, login);
                    return contactMethod.map(UserContactMethod::getUser)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email : " + login));
                });

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));

        return UserPrincipal.create(user);
    }
}