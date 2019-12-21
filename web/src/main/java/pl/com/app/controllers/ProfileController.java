package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.InfoDTO;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.ProfileDTO;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ResponseMessage<List<ProfileDTO>>> findAll() {

        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<ProfileDTO>>builder().data(profileService.findAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<ProfileDTO>> findOne(@PathVariable Long id) {

        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(httpHeaders)
                .body(ResponseMessage.<ProfileDTO>builder().data(profileService.findOne(id)).build());
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage<InfoDTO>> add(RequestEntity<ProfileDTO> requestEntity) {

        ProfileDTO profileFromDB = profileService.add(requestEntity.getBody());
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info(profileFromDB.getName() + " has been added").build()).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage<InfoDTO>> update(@PathVariable Long id,
                                                           @RequestBody ProfileDTO profileDTO) {

        ProfileDTO profileFromDB = profileService.update(id, profileDTO);
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<InfoDTO>builder().data(InfoDTO.builder().info(profileFromDB.getName() + " has been updated").build()).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage<ProfileDTO>> delete(@PathVariable Long id) {
        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<ProfileDTO>builder().data(profileService.delete(id)).build());
    }

    @GetMapping("/custom/{id}")
    public ResponseEntity<ResponseMessage<List<PhoneDTO>>> findAllEntertainmentProfile(@PathVariable Long id) {

        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<PhoneDTO>>builder().data(profileService.findAllByProfile(id)).build());
    }

}
