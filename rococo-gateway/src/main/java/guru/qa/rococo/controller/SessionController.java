package guru.qa.rococo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping
    public Map<String, Object> session(@AuthenticationPrincipal Jwt principal) {
        if (principal == null) {
            return Map.of("authenticated", false);
        }
        return Map.of(
                "authenticated", true,
                "username", principal.getClaim("sub")
        );
    }
}