package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.InfoDTO;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.ProfileParametersDTO;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.ProfileParametersService;

import java.util.List;

@RestController
@RequestMapping("/profile-parameters")
@RequiredArgsConstructor
public class ProfileParametersController {

    private final ProfileParametersService profileParametersService;

    @GetMapping
    public ResponseEntity<ResponseMessage<List<ProfileParametersDTO>>> findAll() {

        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<ProfileParametersDTO>>builder().data(profileParametersService.findAll()).build());
    }

    @GetMapping("/entertainment")
    public ResponseEntity<ResponseMessage<List<PhoneDTO>>> findAllEntertainmentProfile() {

        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<PhoneDTO>>builder().data(profileParametersService.findAllEntertainmentProfile()).build());
    }

    @GetMapping("/business")
    public ResponseEntity<ResponseMessage<List<PhoneDTO>>> findAllBusinessProfile() {

        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<PhoneDTO>>builder().data(profileParametersService.findAllBusinessProfile()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<ProfileParametersDTO>> findOne(@PathVariable Long id) {

        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<ProfileParametersDTO>builder().data(profileParametersService.findOne(id)).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage<InfoDTO>> update(@PathVariable Long id,
                                                           @RequestBody ProfileParametersDTO profileParametersDTO) {


        ProfileParametersDTO profileParametersFromDB = profileParametersService.update(id, profileParametersDTO);
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info(profileParametersFromDB.getId() + " has been updated").build()).build());
    }

}
