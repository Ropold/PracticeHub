package ropold.backend.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;

@RestController
@RequestMapping("/api/users")

public class UserController {

    @GetMapping("/me")
    public String getMe() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

//    @GetMapping("/me")
//    public String getMe(@AuthenticationPrincipal OAuth2User user) {
//        if (user == null) {
//            return "anonymousUser";
//        }
//        return user.getAttributes().get("login").toString(); //github username
//    }
}