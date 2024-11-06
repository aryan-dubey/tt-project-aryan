package com.auriga_tt.controller;

import com.auriga_tt.dto.*;
import com.auriga_tt.exceptions.*;

import com.auriga_tt.security.JwtTokenProvider;
import com.auriga_tt.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO userDTO,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/signup";
        }

        try {
            userService.registerUser(userDTO);
            return "redirect:/auth/login?registered";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/signup";
        }
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Add JWT to cookie
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return "redirect:/dashboard";
        } catch (AuthenticationException e) {
            return "redirect:/auth/login?error";
        }
    }

    @PostMapping("/logout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    tokenProvider.invalidateToken(cookie.getValue());
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        SecurityContextHolder.clearContext();
        return "redirect:/auth/login?logout";
    }
}
