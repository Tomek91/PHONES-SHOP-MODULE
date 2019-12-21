package pl.com.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.ExceptionInfo;
import pl.com.app.exceptions.exceptions.ExceptionMessage;
import pl.com.app.exceptions.rest.ResponseMessage;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException {

        try {

            String token = request.getHeader(SecurityConfig.TOKEN_HEADER);
            if (token != null) {
                var user = TokenService.parseToken(token);
                SecurityContextHolder.getContext().setAuthentication(user);
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
            outputMessage.setStatusCode(HttpStatus.UNAUTHORIZED);

            messageConverter.write(ResponseMessage
                    .builder()
                    .exceptionMessage(
                            ExceptionMessage
                                    .builder()
                                    .exceptionInfo(new ExceptionInfo(ExceptionCode.SECURITY, e.getMessage()))
                                    .path(request.getContextPath())
                                    .build()
                    )
                    .build(), MediaType.APPLICATION_JSON_UTF8, outputMessage);
        }
    }
}
