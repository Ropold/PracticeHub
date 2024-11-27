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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // do not disable CSRF in realworld projects

                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.GET, "/api/practice-hub").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/practice-hub/*").permitAll()
                        .requestMatchers("/api/practice-hub").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/practice-hub/*").authenticated()
                        .requestMatchers("/api/user/me").authenticated()
                        .requestMatchers("/api/secured").authenticated()
                        .anyRequest().permitAll()
                )

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(o -> o.defaultSuccessUrl(appUrl));


        return http.build();
    }

}