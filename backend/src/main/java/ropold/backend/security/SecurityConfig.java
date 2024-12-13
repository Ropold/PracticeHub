package ropold.backend.security;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import ropold.backend.model.AppUser;
import ropold.backend.repository.AppUserRepository;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.url}")
    private String appUrl;

    private final AppUserRepository appUserRepository;

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
                        .requestMatchers(HttpMethod.POST, "api/practice-hub/favorites/{userId}/{roomId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "api/practice-hub/favorites/{userId}/{roomId}").authenticated()
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

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService userService = new DefaultOAuth2UserService();

        return (userRequest) -> {
            OAuth2User githubUser = userService.loadUser(userRequest);

            AppUser user = appUserRepository.findById(githubUser.getName())
                    .orElseGet(() -> {
                        AppUser newUser = new AppUser(
                                githubUser.getName(),
                                githubUser.getAttribute("login"),
                                githubUser.getAttribute("name"),
                                githubUser.getAttribute("avatar_url"),
                                githubUser.getAttribute("html_url"),
                                Collections.emptyList());
                        return appUserRepository.save(newUser);
                    });

            return githubUser; //defaultOauth2User hier vllt?
        };
    }
}