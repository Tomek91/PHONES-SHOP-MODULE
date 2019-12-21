package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.app.dto.InfoDTO;
import pl.com.app.dto.MailDTO;
import pl.com.app.dto.UserResetPasswordDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.model.User;
import pl.com.app.model.VerificationToken;
import pl.com.app.repository.UserRepository;
import pl.com.app.repository.VerificationTokenRepository;
import pl.com.app.service.listeners.OnRemindPasswordEvenData;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public InfoDTO sendRemindPasswordMail(MailDTO mailDTO, HttpServletRequest request) {
        try {
            if (mailDTO == null) {
                throw new NullPointerException("OBJECT IS NULL");
            }
            if (mailDTO.getMail() == null) {
                throw new NullPointerException("MAIL IS NULL");
            }
            Optional<User> userOptional = userRepository.findByEmail(mailDTO.getMail());

            if (userOptional.isEmpty()) {
                throw new NullPointerException("EMAIL NOT EXIST");
            }

            User user = userOptional.get();

            final String url = "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
            eventPublisher.publishEvent(new OnRemindPasswordEvenData(url, user));
            return InfoDTO.builder().info("Remind password: mail was sent").build();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public UserResetPasswordDTO remindPasswordConfirmation(String token) {
        try {
            if (token == null) {
                throw new NullPointerException("TOKEN IS NULL");
            }

            VerificationToken verificationToken
                    = verificationTokenRepository.findByToken(token)
                    .orElseThrow(() -> new NullPointerException("USER WITH TOKEN " + token + " DOESN'T EXIST"));

            if (verificationToken.getExpirationDateTime().isBefore(LocalDateTime.now())) {
                throw new NullPointerException("TOKEN HAS BEEN EXPIRED");
            }

            User user = verificationToken.getUser();

            verificationToken.setToken(null);
            verificationTokenRepository.save(verificationToken);

            return UserResetPasswordDTO.builder().id(user.getId()).build();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public InfoDTO resetRemindPassword(UserResetPasswordDTO userResetPasswordDto) {
        try {
            if (userResetPasswordDto == null) {
                throw new NullPointerException("USER IS NULL");
            }

            User user = userRepository.findById(userResetPasswordDto.getId()).orElseThrow(NullPointerException::new);
            user.setPassword(passwordEncoder.encode(userResetPasswordDto.getPassword()));
            userRepository.save(user);
            return InfoDTO.builder().info("Password has been changed.").build();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void resetPassword(UserResetPasswordDTO userResetPasswordDto) {
        try {
            if (userResetPasswordDto == null) {
                throw new NullPointerException("USER IS NULL");
            }
            if (!userResetPasswordDto.getPassword().equals(userResetPasswordDto.getPasswordConfirmation())) {
                throw new NullPointerException("PASSWORDS ARE NOT THE SAME");
            }
            User user = userRepository.findById(userResetPasswordDto.getId()).orElseThrow(NullPointerException::new);
            user.setPassword(passwordEncoder.encode(userResetPasswordDto.getPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
