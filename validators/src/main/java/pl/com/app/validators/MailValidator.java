package pl.com.app.validators;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.com.app.dto.MailDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.service.UserService;

@Component
@RequiredArgsConstructor
public class MailValidator implements Validator {
    private final UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(MailDTO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            throw new MyException(ExceptionCode.VALIDATION, "OBJECT IS NULL");
        }

        MailDTO mailDTO = (MailDTO) o;

        if (StringUtils.isBlank(mailDTO.getMail())) {
            errors.rejectValue("mail", "Mail is not correct");
        }
        
        if (!errors.hasErrors()) {
            if (userService.findByEmail(mailDTO.getMail()).isEmpty()){
                errors.reject("email_exist", "EMAIL NOT EXIST");
            }
        }
    }
}
