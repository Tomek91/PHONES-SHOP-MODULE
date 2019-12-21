package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.PhonesCompareDataDTO;
import pl.com.app.dto.SpecifiedPhonesDTO;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.PhoneCompareService;

import java.util.List;

@RestController
@RequestMapping("/phones/compare")
@RequiredArgsConstructor
public class PhoneCompareController {

    private final PhoneCompareService phoneCompareService;

    @GetMapping
    public ResponseEntity<ResponseMessage<List<PhoneDTO>>> findAllSorted() {

        HttpHeaders httpHeaders = new HttpHeaders();

        return new ResponseEntity<>(
                ResponseMessage.<List<PhoneDTO>>builder().data(phoneCompareService.findAllSorted()).build(),
                httpHeaders, HttpStatus.OK
        );
    }

    @PostMapping("/specified")
    public ResponseEntity<ResponseMessage<PhonesCompareDataDTO>> findAllSpecified(@RequestBody SpecifiedPhonesDTO specifiedPhonesDTO) {

        HttpHeaders httpHeaders = new HttpHeaders();

        return new ResponseEntity<>(
                ResponseMessage.<PhonesCompareDataDTO>builder().data(phoneCompareService.findAllSpecified(specifiedPhonesDTO)).build(),
                httpHeaders, HttpStatus.OK
        );
    }


}
