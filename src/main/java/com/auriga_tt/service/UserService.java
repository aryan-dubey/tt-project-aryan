package com.auriga_tt.service;

import com.auriga_tt.dto.UserContactMethodDTO;
import com.auriga_tt.dto.UserDTO;
import com.auriga_tt.dto.UserRegistrationDTO;
import com.auriga_tt.exceptions.*;
import com.auriga_tt.model.User;
import com.auriga_tt.model.UserContactMethod;
import com.auriga_tt.repository.UserContactMethodRepository;
import com.auriga_tt.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserContactMethodRepository contactMethodRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileUploadService fileUploadService;

//    @Autowired
//    private JavaMailSender mailSender;

    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFullName(user.getFullName());
        userDTO.setDepartment(user.getDepartment());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        userDTO.setProfileImage(user.getProfileImage());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setLastLogin(user.getLastLogin());
        userDTO.setIsActive(user.getIsActive());

        if (user.getContactMethods() != null) {
            userDTO.setContactMethods(user.getContactMethods().stream()
                    .map(this::convertToContactMethodDTO)
                    .collect(Collectors.toList()));
        }

        return userDTO;
    }

    private UserContactMethodDTO convertToContactMethodDTO(UserContactMethod contactMethod) {
        UserContactMethodDTO dto = new UserContactMethodDTO();
        dto.setContactId(contactMethod.getContactId());
        dto.setContactType(contactMethod.getContactType());
        dto.setContactValue(contactMethod.getContactValue());
        dto.setIsVerified(contactMethod.getIsVerified());
        dto.setIsPrimary(contactMethod.getIsPrimary());
        dto.setCreatedAt(contactMethod.getCreatedAt());
        return dto;
    }

    public User registerUser(UserRegistrationDTO registrationDTO) {
        // Validate if username already exists
        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setFullName(registrationDTO.getFullName());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(registrationDTO.getRole());
        user.setIsActive(User.UserStatus.INACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setLoginRetries(0);
        user.setIsDeleted(false);
        System.out.println("Hi");
        user = userRepository.save(user);

        // Create primary email contact method
        UserContactMethod emailContact = new UserContactMethod();
        emailContact.setUser(user);
        emailContact.setContactType(UserContactMethod.ContactType.EMAIL);
        emailContact.setContactValue(registrationDTO.getEmail());
        emailContact.setIsPrimary(true);
        emailContact.setIsVerified(true);//setting directly to true to skip verification otherwise it should be false
        //emailContact.setVerificationToken(generateVerificationToken());
        //emailContact.setVerificationExpiry(LocalDateTime.now().plusHours(24));
        emailContact.setCreatedAt(LocalDateTime.now());

        contactMethodRepository.save(emailContact);

        // Send verification email
        //sendVerificationEmail(emailContact);

        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    public void addEmailToUser(Long userId, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if email already exists for any user
        if (contactMethodRepository.findByContactTypeAndContactValue(
                UserContactMethod.ContactType.EMAIL, email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        UserContactMethod emailContact = new UserContactMethod();
        emailContact.setUser(user);
        emailContact.setContactType(UserContactMethod.ContactType.EMAIL);
        emailContact.setContactValue(email);
        emailContact.setIsPrimary(false);
        emailContact.setIsVerified(true);//setting directly to true to skip verification otherwise it should be false
        //emailContact.setVerificationToken(generateVerificationToken());
        //emailContact.setVerificationExpiry(LocalDateTime.now().plusHours(24));
        //emailContact.setCreatedAt(LocalDateTime.now());

        contactMethodRepository.save(emailContact);
        //sendVerificationEmail(emailContact);
    }
//    public void verifyEmail(String token) {
//        UserContactMethod contact = contactMethodRepository.findByVerificationToken(token)
//                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));
//
//        if (LocalDateTime.now().isAfter(contact.getVerificationExpiry())) {
//            throw new TokenExpiredException("Verification token has expired");
//        }
//
//        contact.setIsVerified(true);
//        contact.setVerificationToken(null);
//        contact.setVerificationExpiry(null);
//        contactMethodRepository.save(contact);
//
//        // If this is the user's first verified email, activate the account
//        User user = contact.getUser();
//        if (user.getIsActive() == User.UserStatus.INACTIVE) {
//            user.setIsActive(User.UserStatus.ACTIVE);
//            userRepository.save(user);
//        }
//    }
//
//    private String generateVerificationToken() {
//        return UUID.randomUUID().toString();
//    }

//    private void sendVerificationEmail(UserContactMethod emailContact) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(emailContact.getContactValue());
//        message.setSubject("Verify your email address");
//        message.setText("Please click the link below to verify your email:\n\n" +
//                "http://yourdomain.com/verify-email?token=" + emailContact.getVerificationToken());
//        mailSender.send(message);
//    }

    public User updateProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String fileName = fileUploadService.storeFile(file);
        user.setProfileImage(fileName);
        return userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}