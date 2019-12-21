package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.app.authentication.AuthenticationFacade;
import pl.com.app.dto.*;
import pl.com.app.exceptions.rest.ResponseMessage;
import pl.com.app.service.PhoneOrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PhoneOrderController {

    private final PhoneOrderService phoneOrderService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping("add")
    public ResponseEntity<ResponseMessage<PhoneOrderDTO>> addPhoneOrder(@RequestBody PhoneOrderAddDTO phoneOrderAddDTO) {
        HttpHeaders httpHeaders = new HttpHeaders();
        UserDTO loggedUser = authenticationFacade.getLoggedUser();
        return new ResponseEntity<>(
                ResponseMessage.<PhoneOrderDTO>builder().data(phoneOrderService.addPhoneOrder(loggedUser, phoneOrderAddDTO)).build(),
                httpHeaders, HttpStatus.CREATED
        );
    }

    @GetMapping("/current")
    public ResponseEntity<ResponseMessage<PhoneOrderDataDTO>> findCurrentOrder() {
        HttpHeaders httpHeaders = new HttpHeaders();
        UserDTO loggedUser = authenticationFacade.getLoggedUser();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<PhoneOrderDataDTO>builder().data(phoneOrderService.findCurrentOrder(loggedUser)).build());
    }

    @GetMapping("/historical")
    public ResponseEntity<ResponseMessage<List<PhoneOrderDataDTO>>> findHistoricalOrder() {
        HttpHeaders httpHeaders = new HttpHeaders();
        UserDTO loggedUser = authenticationFacade.getLoggedUser();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<List<PhoneOrderDataDTO>>builder().data(phoneOrderService.findHistoricalOrder(loggedUser)).build());
    }

    @DeleteMapping("/current-order/{id}")
    public ResponseEntity<ResponseMessage<OrderDTO>> deleteCurrentOrder(@PathVariable Long id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        UserDTO loggedUser = authenticationFacade.getLoggedUser();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<OrderDTO>builder().data(phoneOrderService.deleteCurrentOrder(id, loggedUser)).build());
    }

    @DeleteMapping("/phone-order/{id}")
    public ResponseEntity<ResponseMessage<PhoneOrderDTO>> deletePhoneOrder(@PathVariable Long id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<PhoneOrderDTO>builder().data(phoneOrderService.deletePhoneOrder(id)).build());
    }

    @PostMapping("/phone-order/{phoneOrderId}/quantity/{quantity}")
    public ResponseEntity<ResponseMessage<PhoneOrderDTO>> changePhoneOrderQuantity(@PathVariable("phoneOrderId") Long phoneOrderId,
                                                                                   @PathVariable("quantity") Integer quantity) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<PhoneOrderDTO>builder().data(phoneOrderService.changePhoneOrderQuantity(quantity, phoneOrderId)).build());
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<ResponseMessage<InfoDTO>> realiseOrder(@PathVariable("orderId") Long orderId,
                                                                 HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        UserDTO loggedUser = authenticationFacade.getLoggedUser();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<InfoDTO>builder().data(phoneOrderService.realiseOrder(orderId, loggedUser, request)).build());
    }

    @GetMapping("/orderConfirmation")
    public ResponseEntity<ResponseMessage<OrderDTO>> confirmOrder(@RequestParam("token") String token,
                                                                  HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(ResponseMessage.<OrderDTO>builder().data(phoneOrderService.confirmOrder(token, request)).build());
    }


}
