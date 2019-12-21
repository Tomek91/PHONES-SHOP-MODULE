package pl.com.app.authentication;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    boolean isUserLoggedIn();
}
