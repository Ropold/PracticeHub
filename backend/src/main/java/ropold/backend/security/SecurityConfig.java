package ropold.backend.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.url}")
    private String appUrl;

    private static final String PRACTICE_HUB_PATH = "/api/practice-hub/*";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // do not disable CSRF in realworld projects

                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.GET, "/api/practice-hub").permitAll()
                        .requestMatchers(HttpMethod.GET, PRACTICE_HUB_PATH).permitAll() // Verwendung der Konstante
                        .requestMatchers(HttpMethod.PUT, PRACTICE_HUB_PATH).authenticated() // Verwendung der Konstante
                        .requestMatchers(HttpMethod.DELETE, PRACTICE_HUB_PATH).authenticated() // Verwendung der Konstante
                        .requestMatchers("/api/practice-hub").authenticated()
                        .requestMatchers("/api/users/me").permitAll()
                        .anyRequest().permitAll()
                )
                .logout(l -> l.logoutUrl("/api/users/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(200)))

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(o -> o.defaultSuccessUrl(appUrl));


        return http.build();
    }

}