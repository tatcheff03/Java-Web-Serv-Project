package com.example.medicalrecord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import java.util.HashSet;

@Configuration
public class SecurityConfig {
    @Bean
    public GrantedAuthoritiesMapper keycloakAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            for (GrantedAuthority authority : authorities) {
                mappedAuthorities.add(authority); // OIDC_USER, SCOPE_xx

                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    Map<String, Object> userAttributes = oidcUserAuthority.getAttributes();
                    Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get("realm_access");

                    if (realmAccess != null && realmAccess.containsKey("roles")) {
                        List<String> roles = (List<String>) realmAccess.get("roles");
                        for (String role : roles) {
                            mappedAuthorities.add(new SimpleGrantedAuthority(role));
                        }
                    }
                }
            }

            return mappedAuthorities;
        };
    }

    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();

        converter.setAuthoritiesClaimName("realm_access.roles");
        converter.setAuthorityPrefix("ROLE_");

        System.out.println("✅ JwtGrantedAuthoritiesConverter set with claim 'realm_access.roles'");
        return converter;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        System.out.println("✅ JwtAuthenticationConverter wired");
        return converter;

    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("http://localhost:8080/realms/medical-record");
    }


    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/unauthorized").permitAll()
                        .requestMatchers("/patients/history").hasRole("PATIENT")
                        .requestMatchers("/patients", "/patients/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/patients/delete/**", "/doctors/**", "/reports/**").hasRole("ADMIN")
                        .requestMatchers("/patients/create", "/patients/edit/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/visits/**", "/treatment/**", "/sick-leaves/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/diagnoses/**", "/medicines/**").hasAnyRole("DOCTOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(keycloakAuthoritiesMapper())
                        )
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .exceptionHandling(exception->exception
                        .accessDeniedPage("/unauthorized")
                )
                .logout(logout -> logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                                new SecurityContextLogoutHandler().logout(request, response, authentication);
                                String idToken = oidcUser.getIdToken().getTokenValue();
                                String logoutUrl = "http://localhost:8080/realms/medical-record/protocol/openid-connect/logout"
                                        + "?id_token_hint=" + idToken
                                        + "&post_logout_redirect_uri=http://localhost:8081/";
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


