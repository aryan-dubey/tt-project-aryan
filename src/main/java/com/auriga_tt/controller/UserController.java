//package com.auriga_tt.controller;
//
//import com.auriga_tt.dto.*;
//import com.auriga_tt.exceptions.*;
//import com.auriga_tt.model.RefreshToken;
//import com.auriga_tt.model.User;
//import com.auriga_tt.repository.UserRepository;
//import com.auriga_tt.security.JwtTokenProvider;
//import com.auriga_tt.model.UserPrincipal;
//import com.auriga_tt.service.UserService;
//import com.auriga_tt.service.RefreshTokenService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@Validated
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private JwtTokenProvider tokenProvider;
//
//    @Autowired
//    private RefreshTokenService refreshTokenService;
//
//    @GetMapping("/signup")
//    public String showSignupForm(Model model) {
//        model.addAttribute("user", new UserRegistrationDTO());
//        return "auth/signup";
//    }
//
//    @PostMapping("/signup")
//    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO userDTO,
//                               BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            return "auth/signup";
//        }
//
//        try {
//            userService.registerUser(userDTO);
//            model.addAttribute("message", "Registration successful! Please check your email to verify your account.");
//            return "auth/login";
//        } catch (UserAlreadyExistsException e) {
//            result.rejectValue("username", "error.user", "Username already exists");
//            return "auth/signup";
//        }
//    }
//
//    @GetMapping("/login")
//    public String showLoginForm() {
//        return "auth/login";  // This will render the login.html template
//    }
//
//    @PostMapping("/login")
//    @ResponseBody
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getUsernameOrEmail(),
//                        loginRequest.getPassword()
//                )
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String jwt = tokenProvider.generateToken(authentication);
//        System.out.println(jwt);
//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(((UserPrincipal) authentication.getPrincipal()).getId());
//
//        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, refreshToken.getToken()));
//    }
//
//    @PostMapping("auth/refreshtoken")
//    @ResponseBody
//    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
//        String requestRefreshToken = request.getRefreshToken();
//
//        return refreshTokenService.findByToken(requestRefreshToken)
//                .map(refreshTokenService::verifyExpiration)
//                .map(RefreshToken::getUser)
//                .map(user -> {
//                    String token = tokenProvider.generateTokenFromUsername(user.getUsername());
//                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
//                })
//                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
//                        "Refresh token is not in database!"));
//    }
//
//    @PostMapping("/signout")
//    @ResponseBody
//    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
//        refreshTokenService.deleteByUserId(logOutRequest.getUserId());
//        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
//    }
//
////    @GetMapping("/verify-email")
////    public String verifyEmail(@RequestParam String token, Model model) {
////        try {
////            userService.verifyEmail(token);
////            model.addAttribute("message", "Email verified successfully! You can now log in.");
////            return "auth/login";
////        } catch (InvalidTokenException | TokenExpiredException e) {
////            model.addAttribute("error", e.getMessage());
////            return "verify-email-error";
////        }
////    }
//
//    @GetMapping("/add-email")
//    @PreAuthorize("isAuthenticated()")
//    public String showAddEmailForm(Model model) {
//        model.addAttribute("emailDto", new AddEmailDTO());
//        return "add-email";
//    }
//
//    @PostMapping("/add-email")
//    @PreAuthorize("isAuthenticated()")
//    public String addEmail(@Valid @ModelAttribute("emailDto") AddEmailDTO emailDto,
//                           BindingResult result, Model model,
//                           @AuthenticationPrincipal UserDetails userDetails) {
//        if (result.hasErrors()) {
//            return "add-email";
//        }
//
//        try {
//            User user = userService.getUserByUsername(userDetails.getUsername());
//            userService.addEmailToUser(user.getUserId(), emailDto.getEmail());
//            model.addAttribute("message", "Verification email sent to " + emailDto.getEmail());
//            return "add-email-success";
//        } catch (EmailAlreadyExistsException e) {
//            result.rejectValue("email", "error.email", "Email already registered");
//            return "add-email";
//        }
//    }
//}