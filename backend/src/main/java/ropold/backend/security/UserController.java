package ropold.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ropold.backend.model.AppUser;
import ropold.backend.repository.AppUserRepository;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AppUserRepository appUserRepository;

    @GetMapping("me")
    public String getMe(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return "anonymousUser";
        } else {
            Optional<AppUser> appUserOpt = appUserRepository.findById(user.getName());
            return appUserOpt.map(AppUser::id).orElse(SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    @GetMapping("/me/details")
    public Map<String, Object> getUserDetails(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return Map.of("message", "User not authenticated");
        }
        return user.getAttributes();
    }
}
