package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.app.dto.InfoDTO;
import pl.com.app.dto.UserDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.UserMapper;
import pl.com.app.model.Role;
import pl.com.app.model.User;
import pl.com.app.model.VerificationToken;
import pl.com.app.repository.RoleRepository;
import pl.com.app.repository.UserRepository;
import pl.com.app.repository.VerificationTokenRepository;
import pl.com.app.service.listeners.OnRegistrationEvenData;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final FileService fileService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;


    public void createVerificationToken(User user, String token) {
        try {
            if (user == null) {
                throw new NullPointerException("USER IS NULL");
            }

            if (token == null) {
                throw new NullPointerException("TOKEN IS NULL");
            }

            VerificationToken verificationToken = new VerificationToken();
            Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByUserId_Equals(user.getId());
            if (verificationTokenOpt.isPresent()){
                verificationToken = verificationTokenOpt.get();
            }

            verificationToken.setUser(userRepository.getOne(user.getId()));
            verificationToken.setToken(token);
            verificationToken.setExpirationDateTime(LocalDateTime.now().plusDays(1L));

            verificationTokenRepository.save(verificationToken);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public InfoDTO confirmRegistration(String token) {
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
            user.setActive(true);
            userRepository.save(user);

            verificationToken.setToken(null);
            verificationTokenRepository.save(verificationToken);

            return InfoDTO.builder().info(user.getUserName() + " registration confirmation correctly.").build();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public InfoDTO registerNewUser(UserDTO userDTO, HttpServletRequest request) {
        try {
            if (userDTO == null) {
                throw new NullPointerException("USER OBJECT IS NULL");
            }
            if (userDTO.getRoleDTO() == null) {
                throw new NullPointerException("ROLE OBJECT IS NULL");
            }
            if (userDTO.getRoleDTO().getName() == null) {
                throw new NullPointerException("ROLE NAME IS NULL");
            }

            Role role = roleRepository
                    .findByName(userDTO.getRoleDTO().getName())
                    .orElseThrow(NullPointerException::new);

            User user = userMapper.userDTOToUser(userDTO);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setRole(role);
            user.setEmail(userDTO.getEmail());
            user.setName(userDTO.getName());
            user.setSurname(userDTO.getSurname());
            user.setUserName(userDTO.getUserName());
            user.setActive(false);
            userRepository.save(user);

            final String url = "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationEvenData(url, user));

            return InfoDTO.builder().info(user.getUserName() + " has been registered").build();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
