package com.auriga_tt.security;

import com.auriga_tt.model.CustomOAuth2User;
import com.auriga_tt.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            User user = oauthUser.getUser();

            // Set a cookie to recommend Google login for future attempts
            if ("google".equals(user.getOAuth2Provider())) {
                Cookie recommendGoogleLogin = new Cookie("recommend_google_login", "true");
                recommendGoogleLogin.setMaxAge(30 * 24 * 60 * 60); // 30 days
                recommendGoogleLogin.setPath("/");
                recommendGoogleLogin.setHttpOnly(true);
                recommendGoogleLogin.setSecure(request.isSecure()); // Set secure flag if using HTTPS
                response.addCookie(recommendGoogleLogin);
            }
        }

        // Redirect to the dashboard or home page after successful authentication
        getRedirectStrategy().sendRedirect(request, response, "/dashboard");
    }
}