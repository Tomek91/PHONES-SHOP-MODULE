package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.PhonesCustomFilterDataDTO;
import pl.com.app.dto.PhonesFilterDataDTO;
import pl.com.app.dto.ProducerDTO;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.PhoneService;

import java.util.List;

@RestController
@RequestMapping("/phones/filter")
@RequiredArgsConstructor
public class PhoneFilterController {

    private final PhoneService phoneService;


    @GetMapping("/producers")
    public ResponseEntity<ResponseMessage<List<ProducerDTO>>> findAllPhoneProducers() {

        HttpHeaders httpHeaders = new HttpHeaders();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<ProducerDTO>>builder().data(phoneService.findAllPhoneProducers()).build());
    }

    @PostMapping("/producers/models")
    public ResponseEntity<ResponseMessage<List<PhoneDTO>>> findAllPhoneByProducers(@RequestBody List<ProducerDTO> producerDTOList) {

        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<PhoneDTO>>builder().data(phoneService.findAllPhoneByProducers(producerDTOList)).build());
    }

    @PostMapping("/producers/models/custom")
    public ResponseEntity<ResponseMessage<List<PhoneDTO>>> findAllChosePhones(@RequestBody List<PhoneDTO> phoneDTOList) {

        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<PhoneDTO>>builder().data(phoneService.findAllChosePhones(phoneDTOList)).build());

    }

    @PostMapping("/producers/models/custom/filter")
    public ResponseEntity<ResponseMessage<PhonesCustomFilterDataDTO>> findAllFilterPhones(@RequestBody PhonesFilterDataDTO phonesFilterDataDTO) {

        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<PhonesCustomFilterDataDTO>builder().data(phoneService.findAllFilterPhones(phonesFilterDataDTO)).build());
    }


}
