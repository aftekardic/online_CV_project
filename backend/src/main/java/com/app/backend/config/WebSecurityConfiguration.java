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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.app.backend.business.service.CustomAccessDenied;
import com.app.backend.business.service.CustomAuthenticationEntryPoint;
import com.app.backend.business.service.KeycloakJwtRolesConverter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
        @Value("${keycloak.client-id}")
        private String kcClientId;

        @Value("${keycloak.token-issuer-url}")
        private String tokenIssuerUrl;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint entryPoint,
                        CustomAccessDenied accessDenied) throws Exception {

                DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
                                new JwtGrantedAuthoritiesConverter(),
                                new KeycloakJwtRolesConverter(kcClientId));

                http.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/v1/cv/**").hasAnyRole("USER_READ", "ADMIN_WRITE")
                                                .requestMatchers("/api/v1/user/**")
                                                .hasAnyRole("USER_READ", "ADMIN_WRITE")
                                                .requestMatchers("/auth/**").permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(entryPoint)
                                                .accessDeniedHandler(accessDenied))
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(jwtAuthenticationConverter(
                                                                                authoritiesConverter))));

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

        private JwtAuthenticationConverter jwtAuthenticationConverter(
                        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter) {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
                return converter;
        }

        @Bean
        public CorsFilter corsFilter() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(true);
                config.addAllowedOrigin("http://localhost:3000");
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return new CorsFilter(source);
        }
}
