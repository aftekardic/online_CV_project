package com.app.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.app.backend.business.service.CustomAccessDenied;
import com.app.backend.business.service.CustomAuthenticationEntryPoint;
import com.app.backend.business.service.KeycloakJwtRolesConverter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Value("auth-service-v1")
    private String kcClientId;

    @Value("http://localhost:8080/realms/online-cv-project-realm")
    private String tokenIssuerUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint entryPoint,
            CustomAccessDenied accessDenied) throws Exception {

        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(),
                new KeycloakJwtRolesConverter(kcClientId));

        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/home/admin/**")
                .hasRole("ADMIN_WRITE")
                .requestMatchers("/home/public/**")
                .hasRole("USER_READ")
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated())
                .httpBasic()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(accessDenied)
                .and()
                .csrf().disable()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(
                        jwt -> new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt)));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(tokenIssuerUrl);
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
