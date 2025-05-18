package com.example.medicalrecord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/unauthorized").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {

                                new SecurityContextLogoutHandler().logout(request, response, authentication);


                                String idToken = oidcUser.getIdToken().getTokenValue();


                                String logoutUrl = "http://localhost:8080/realms/medical-record/protocol/openid-connect/logout"
                                        + "?id_token_hint=" + idToken
                                        + "&post_logout_redirect_uri=http://localhost:8081/oauth2/authorization/keycloak";

                                response.sendRedirect(logoutUrl);
                            } else {

                                response.sendRedirect("/");
                            }
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}