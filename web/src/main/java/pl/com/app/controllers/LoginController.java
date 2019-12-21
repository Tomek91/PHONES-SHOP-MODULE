package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.com.app.security.TokenService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.InfoDTO;
import pl.com.app.dto.MailDTO;
import pl.com.app.dto.TokensDTO;
import pl.com.app.dto.UserResetPasswordDTO;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.LoginService;
import pl.com.app.validators.MailValidator;
import pl.com.app.validators.PasswordsValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/security")
public class LoginController {

    private final LoginService loginService;
    private final MailValidator mailValidator;
    private final PasswordsValidator passwordsValidator;

    @InitBinder("mailDTO")
    private void initMailBinder(WebDataBinder binder) {
        binder.setValidator(mailValidator);
    }

    @InitBinder("userResetPasswordDto")
    private void initPasswordBinder(WebDataBinder binder) {
        binder.setValidator(passwordsValidator);
    }

    @PostMapping("/remind-password")
    public ResponseEntity<ResponseMessage<InfoDTO>> remindPassword(@Valid @RequestBody MailDTO mailDTO,
                                                                   BindingResult bindingResult,
                                                                   HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            String fieldErrors = bindingResult
                    .getFieldErrors()
                    .stream()
                    .map(e -> String.join(" : ", e.getField(), e.getCode()))
                    .collect(Collectors.joining(" "));

            String globalErrors = bindingResult
                    .getGlobalErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(" "));
            String errors = String.join(" ", fieldErrors, globalErrors);

            return ResponseEntity.ok(ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info("remind password validation errors: " + errors).build()).build());
        }

        return new ResponseEntity<>(ResponseMessage.<InfoDTO>builder().data(loginService.sendRemindPasswordMail(mailDTO, request)).build(), null, HttpStatus.OK);
    }

    @GetMapping("/remind-password/reset")
    public ResponseEntity<ResponseMessage<UserResetPasswordDTO>> remindPasswordConfirmation(@RequestParam("token") String token) {

        return new ResponseEntity<>(ResponseMessage.<UserResetPasswordDTO>builder().data(loginService.remindPasswordConfirmation(token)).build(),
                null,
                HttpStatus.OK);
    }

    @PostMapping("/remind-password/reset")
    public ResponseEntity<ResponseMessage<InfoDTO>> remindPasswordConfirmation(@Valid @RequestBody UserResetPasswordDTO userResetPasswordDto,
                                                                               BindingResult bindingResult) {

        passwordsValidator.validate(userResetPasswordDto, bindingResult);
        if (bindingResult.hasErrors()) {
            String fieldErrors = bindingResult
                    .getFieldErrors()
                    .stream()
                    .map(e -> String.join(" : ", e.getField(), e.getCode()))
                    .collect(Collectors.joining(" "));

            String globalErrors = bindingResult
                    .getGlobalErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(" "));
            String errors = String.join(" ", fieldErrors, globalErrors);

            return ResponseEntity.ok(ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info("remind password validation errors: " + errors).build()).build());
        }

        return new ResponseEntity<>(ResponseMessage.<InfoDTO>builder().data(loginService.resetRemindPassword(userResetPasswordDto)).build(), null, HttpStatus.OK);
    }


    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseMessage<TokensDTO>> generateNewToken(@RequestParam("token") String refreshToken) {

        return new ResponseEntity<>(ResponseMessage.<TokensDTO>builder().data(TokenService.generateToken(refreshToken)).build(), null, HttpStatus.OK);
    }
}
