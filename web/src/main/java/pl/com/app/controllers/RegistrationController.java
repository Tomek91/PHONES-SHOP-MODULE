package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.InfoDTO;
import pl.com.app.dto.UserDTO;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.RegistrationService;
import pl.com.app.validators.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserValidator userValidator;

    @InitBinder
    private void intBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(userValidator);
    }

    @PostMapping("/new")
    public ResponseEntity<ResponseMessage<InfoDTO>> addUser(@Valid @RequestBody UserDTO userDTO,
                                                            BindingResult bindingResult,
                                                            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            String fieldErrors = bindingResult
                    .getFieldErrors()
                    .stream()
                    .map(e -> String.join(" : ", e.getField(), e.getCode()))
                    .collect(Collectors.joining(" \n"));

            String globalErrors = bindingResult
                    .getGlobalErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(" \n"));
            String errors = String.join(" \n",fieldErrors, globalErrors);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(null)
                    .body(ResponseMessage.<InfoDTO>builder().data( InfoDTO.builder().info("add user validation errors: " + errors).build()).build());

        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(null)
                .body(ResponseMessage.<InfoDTO>builder().data(registrationService.registerNewUser(userDTO, request)).build());
    }


    @GetMapping("/registerConfirmation")
    public ResponseEntity<ResponseMessage<InfoDTO>> registrationConfirmation(@RequestParam String token) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(null)
                .body(ResponseMessage.<InfoDTO>builder().data(registrationService.confirmRegistration(token)).build());
    }
}
