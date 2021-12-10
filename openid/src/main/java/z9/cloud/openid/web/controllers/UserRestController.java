package z9.cloud.openid.web.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z9.cloud.openid.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserRestController {

    private UserService service;

    public UserRestController(UserService service) {
        this.service = service;
    }

    @GetMapping("/oidc-principal")
    public OidcUser getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
        return principal;
    }

    @GetMapping("/oidc-claims")
    public Map<String, Object> getClaimsFromBean() {
        return service.getUserClaims();
    }
}
