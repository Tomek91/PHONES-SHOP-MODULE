package pl.com.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.com.app.dto.AuthenticationUserDTO;
import pl.com.app.dto.TokensDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.ExceptionInfo;
import pl.com.app.exceptions.exceptions.ExceptionMessage;
import pl.com.app.exceptions.rest.ResponseMessage;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        try {

            AuthenticationUserDTO user
                    = new ObjectMapper().readValue(
                    request.getInputStream(), AuthenticationUserDTO.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.emptyList()));

        } catch (Exception e) {
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(
                        new ObjectMapper().writeValueAsString(
                                ResponseMessage
                                        .builder()
                                        .exceptionMessage(
                                                ExceptionMessage
                                                        .builder()
                                                        .exceptionInfo(new ExceptionInfo(ExceptionCode.SECURITY, "attempt authentication: " + e.getMessage()))
                                                        .path(request.getContextPath())
                                                        .build()
                                        )
                                        .build()
                        )
                );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        ResponseMessage
                                .<TokensDTO>builder()
                                .data(TokenService.generateTokens(authResult))
                                .build()
                )
        );
        response.getWriter().flush();
        response.getWriter().close();

    }


}
