package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import pl.com.app.dto.UserDTO;
import pl.com.app.dto.UserResetPasswordDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.UserMapper;
import pl.com.app.model.User;
import pl.com.app.model.VerificationToken;
import pl.com.app.repository.UserRepository;
import pl.com.app.repository.VerificationTokenRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserMapper userMapper;


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
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public void resetRemindPassword(UserResetPasswordDTO userResetPasswordDto) {
        try {
            if (userResetPasswordDto == null) {
                throw new NullPointerException("USER IS NULL");
            }
            if (!userResetPasswordDto.getPassword().equals(userResetPasswordDto.getPasswordConfirmation())) {
                throw new NullPointerException("PASSWORDS ARE NOT THE SAME");
            }
            User user = userRepository.findById(userResetPasswordDto.getId()).orElseThrow(NullPointerException::new);
            user.setPassword(userResetPasswordDto.getPassword());
            userRepository.save(user);

        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public Optional<UserDTO> findByUserName(String userName) {
        try {
            if (userName == null) {
                throw new NullPointerException("USERNAME IS NULL");
            }
            return userRepository
                    .findByUserName(userName)
                    .map(userMapper::userToUserDTO);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }

    }

    public Optional<UserDTO> findByEmail(String email) {
        try {
            if (email == null) {
                throw new NullPointerException("EMAIL IS NULL");
            }
            return userRepository
                    .findByEmail(email)
                    .map(userMapper::userToUserDTO);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public UserDTO getOneUser(Long userId) {
        try {
            if (userId == null) {
                throw new NullPointerException("USER ID IS NULL");
            }

            return userRepository
                    .findById(userId)
                    .map(userMapper::userToUserDTO)
                    .orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
