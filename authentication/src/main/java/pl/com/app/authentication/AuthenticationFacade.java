package pl.com.app.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.com.app.dto.UserDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.UserMapper;
import pl.com.app.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade implements IAuthenticationFacade {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserDTO getLoggedUser(){
        return userRepository
                .findByUserName(getAuthentication().getName())
                .map(userMapper::userToUserDTO)
                .orElseThrow(() -> new MyException(ExceptionCode.SERVICE, "USERNAME NOT FOUND"));
    }

    @Override
    public boolean isUserLoggedIn(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
    }
}
