package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.InfoDTO;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.PhoneService;
import pl.com.app.validators.PhoneValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/phones")
@RequiredArgsConstructor
public class PhoneController {

    private final PhoneService phoneService;
    private final PhoneValidator phoneValidator;

    @InitBinder
    private void intBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(phoneValidator);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<List<PhoneDTO>>> findAll() {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(
                ResponseMessage.<List<PhoneDTO>>builder().data(phoneService.findAll()).build(),
                httpHeaders, HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<PhoneDTO>> findOne(@PathVariable Long id) {

        HttpHeaders httpHeaders = new HttpHeaders();


        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<PhoneDTO>builder().data(phoneService.findOne(id)).build());
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage<InfoDTO>> add(@Valid @RequestBody PhoneDTO phoneDTO,
                                                        BindingResult bindingResult) {
        if ( bindingResult.hasErrors() ) {
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

            return ResponseEntity.ok(
                    ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info("add phone validation errors\n " + errors).build()).build());
        }

        PhoneDTO phoneFromDB = phoneService.add(phoneDTO);
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info(phoneFromDB.getModel() + " has been added").build()).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage<InfoDTO>> update(@PathVariable Long id,
                                                           @Valid @RequestBody PhoneDTO phoneDTO,
                                                           BindingResult bindingResult) {
        if ( bindingResult.hasErrors() ) {
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

            return ResponseEntity.ok(
                    ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info("update phone validation errors\n " + errors).build()).build());
        }

        PhoneDTO phoneFromDB = phoneService.update(id, phoneDTO);
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info(phoneFromDB.getModel() + " has been updated").build()).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage<PhoneDTO>> delete(@PathVariable Long id) {
        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<PhoneDTO>builder().data(phoneService.delete(id)).build());
    }

}
