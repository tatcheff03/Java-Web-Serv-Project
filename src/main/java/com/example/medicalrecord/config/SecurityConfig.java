package com.example.medicalrecord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
                mappedAuthorities.add(authority);

                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    Map<String, Object> userAttributes = oidcUserAuthority.getAttributes();
                    Object realmAccessObj = userAttributes.get("realm_access");
                    if (realmAccessObj instanceof Map<?, ?> realmAccessMap) {
                        Object rolesObj = realmAccessMap.get("roles");
                        if (rolesObj instanceof List<?> roleList) {
                            for (Object roleObj : roleList) {
                                if (roleObj instanceof String role) {
                                    mappedAuthorities.add(new SimpleGrantedAuthority(role));
                                }
                            }
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
        http.securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                //начална страница
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll().requestMatchers("/", "/unauthorized").permitAll().requestMatchers("/treatment/debug", "treatment/debug/jwt", "treatment/userinfo").permitAll()

                //админ действия
                .requestMatchers(HttpMethod.GET, "/patients/create").permitAll()

                .requestMatchers("/patients/delete/**").hasRole("ADMIN").requestMatchers("/reports/**").hasRole("ADMIN").requestMatchers("/doctors/**").hasRole("ADMIN")

                //лекар лични действия
                .requestMatchers("/treatment/doctor", "/diagnoses/doctor", "/sick-leaves/doctor").hasRole("DOCTOR")

                // Пациент редакция
                .requestMatchers("/patients/edit/**").hasAnyRole("DOCTOR", "ADMIN")

                //  заявки за други (Visits, Treatments, Sick-Leaves,Medicines, Diagnoses
                .requestMatchers("/visits/**", "/treatment/**", "/sick-leaves/**").hasAnyRole("DOCTOR", "ADMIN").requestMatchers("/diagnoses/**", "/medicines/**").hasAnyRole("DOCTOR", "ADMIN")

                //  Глобален достъп до списъци
                .requestMatchers("/patients").hasAnyRole("DOCTOR", "ADMIN").requestMatchers("/doctors", "/doctors/").hasAnyRole("DOCTOR", "ADMIN")





                .anyRequest().authenticated()).oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(keycloakAuthoritiesMapper()))).oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))).exceptionHandling(exception -> exception.accessDeniedPage("/unauthorized")).logout(logout -> logout.logoutSuccessHandler((request, response, authentication) -> {
            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                String idToken = oidcUser.getIdToken().getTokenValue();
                String logoutUrl = "http://localhost:8080/realms/medical-record/protocol/openid-connect/logout" + "?id_token_hint=" + idToken + "&post_logout_redirect_uri=http://localhost:8081/";
                response.sendRedirect(logoutUrl);
            } else {
                response.sendRedirect("/");
            }
        }).invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID"));

        return http.build();
    }
}


