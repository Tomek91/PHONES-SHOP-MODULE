package pl.com.app.validators;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.com.app.dto.UserDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.service.UserService;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {
    private final UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UserDTO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            throw new MyException(ExceptionCode.VALIDATION, "OBJECT IS NULL");
        }

        UserDTO userDTO = (UserDTO) o;

        if (StringUtils.isBlank(userDTO.getName()) || !userDTO.getName().matches("[A-Z ]+")) {
            errors.rejectValue("name", "Name is not correct");
        }
        if (StringUtils.isBlank(userDTO.getSurname()) || !userDTO.getSurname().matches("[A-Za-z ]+")) {
            errors.rejectValue("surname", "Surname is not correct");
        }
        if (StringUtils.isBlank(userDTO.getUserName())) {
            errors.rejectValue("userName", "UserName is not correct");
        }
        if (StringUtils.isBlank(userDTO.getPassword())) {
            errors.rejectValue("password", "Password is not correct");
        }
        if (StringUtils.isBlank(userDTO.getPassword())) {
            errors.rejectValue("passwordConfirmation", "Password confirmation is not correct");
        }
        if (StringUtils.isBlank(userDTO.getEmail()) || !userDTO.getEmail().matches(".+@\\w+.pl")) {
            errors.rejectValue("email", "Email is not correct");
        }

        if (!errors.hasErrors()) {
            if (userService.findByEmail(userDTO.getEmail()).isPresent()){
                errors.reject("email_unique", "EMAIL ALREADY EXISTS");
            }
            if (userService.findByUserName(userDTO.getUserName()).isPresent()){
                errors.reject("email_unique", "USERNAME ALREADY EXISTS");
            }
            if (!Objects.equals(userDTO.getPassword(), userDTO.getPasswordConfirmation())){
                errors.reject("passwords_same", "PASSWORDS ARE NOT THE SAME");
            }
        }
    }
}


