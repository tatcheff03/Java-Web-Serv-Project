package com.example.medicalrecord.web.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;




import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;

import org.springframework.security.core.Authentication;


import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }


        String idToken = "";
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
            idToken = oidcUser.getIdToken().getTokenValue();
        }


        String keycloakLogoutUrl = "http://localhost:8080/realms/medical-record/protocol/openid-connect/logout"
                + "?id_token_hint=" + idToken
                + "&post_logout_redirect_uri=http://localhost:8081/";

        return "redirect:" + keycloakLogoutUrl;
    }
}