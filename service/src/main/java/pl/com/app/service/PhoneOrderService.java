package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import pl.com.app.dto.*;
import pl.com.app.exceptions.exceptions.AccessDeniedException;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.OrderMapper;
import pl.com.app.mappers.PhoneOrderMapper;
import pl.com.app.mappers.UserMapper;
import pl.com.app.model.*;
import pl.com.app.repository.*;
import pl.com.app.service.listeners.OnCompletedOrderEvenData;
import pl.com.app.service.listeners.OnConfirmationOrderEvenData;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneOrderService {

    private final PhoneOrderRepository phoneOrderRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PhoneRepository phoneRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PhoneOrderMapper phoneOrderMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final QrCodeGeneratorService qrCodeGeneratorService;
    private final AmazonService amazonService;


    public PhoneOrderDTO addPhoneOrder(UserDTO loggedUser, PhoneOrderAddDTO phoneOrderAddDTO) {
        try {
            if (loggedUser == null) {
                throw new NullPointerException("LOGGED USER IS NULL");
            }
            if (phoneOrderAddDTO == null) {
                throw new NullPointerException("PHONE ORDER ADD IS NULL");
            }
            if (phoneOrderAddDTO.getPhoneDTO() == null) {
                throw new NullPointerException("PHONE IS NULL");
            }
            if (phoneOrderAddDTO.getPhoneDTO().getId() == null) {
                throw new NullPointerException("PHONE ID IS NULL");
            }
            User user = userRepository.findById(loggedUser.getId()).orElseThrow(NullPointerException::new);
            Phone phone = phoneRepository.findById(phoneOrderAddDTO.getPhoneDTO().getId()).orElseThrow(NullPointerException::new);
            Optional<Order> orderOpt = orderRepository.findByUserId_EqualsAndIsCompletedFalse(loggedUser.getId());
            Order userOrder = null;
            PhoneOrder phoneOrderToSave = null;
            if (orderOpt.isPresent()) {
                userOrder = orderOpt.get();

                PhoneOrder probe = PhoneOrder.builder()
                        .order(userOrder)
                        .phone(phone)
                        .build();

                Example<PhoneOrder> example = Example.of(probe);

                phoneOrderToSave = phoneOrderRepository.findOne(example)
                        .stream()
                        .peek(x -> x.setQuantity(x.getQuantity() + phoneOrderAddDTO.getQuantity()))
                        .findFirst()
                        .orElse(PhoneOrder.builder()
                                .phone(phone)
                                .quantity(phoneOrderAddDTO.getQuantity())
                                .order(userOrder)
                                .build()
                        );
            } else {
                userOrder = orderRepository.save(Order.builder().isCompleted(Boolean.FALSE).user(user).build());
                phoneOrderToSave = PhoneOrder.builder()
                        .phone(phone)
                        .quantity(phoneOrderAddDTO.getQuantity())
                        .order(userOrder)
                        .build();
            }

            PhoneOrder phoneOrderFromDb = phoneOrderRepository.save(phoneOrderToSave);
            return phoneOrderMapper.phoneOrderToPhoneOrderDTO(phoneOrderFromDb);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public PhoneOrderDataDTO findCurrentOrder(UserDTO loggedUser) {
        try {
            if (loggedUser == null) {
                throw new NullPointerException("LOGGED USER IS NULL");
            }
            PhoneOrderDataDTO orderDataDTO = new PhoneOrderDataDTO();
            orderRepository.findByUserId_EqualsAndIsCompletedFalse(loggedUser.getId())
                    .ifPresent(x -> {
                        orderDataDTO.setOrderDTO(orderMapper.orderToOrderDTO(x));
                        orderDataDTO.setPhoneOrderDTOs(phoneOrderRepository
                                .findAllByOrder(x)
                                .stream()
                                .map(phoneOrderMapper::phoneOrderToPhoneOrderDTO)
                                .collect(Collectors.toSet()));
                    });

            return orderDataDTO;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<PhoneOrderDataDTO> findHistoricalOrder(UserDTO loggedUser) {
        try {
            if (loggedUser == null) {
                throw new NullPointerException("LOGGED USER IS NULL");
            }
            User user = userRepository.findById(loggedUser.getId()).orElseThrow(NullPointerException::new);

            return orderRepository.findAllByUserAndIsCompletedTrueOrderByDateTime(user)
                    .stream()
                    .map(x -> PhoneOrderDataDTO.builder()
                            .orderDTO(orderMapper.orderToOrderDTO(x))
                            .phoneOrderDTOs(phoneOrderRepository
                                    .findAllByOrder(x)
                                    .stream()
                                    .map(phoneOrderMapper::phoneOrderToPhoneOrderDTO)
                                    .collect(Collectors.toSet()))
                            .build()
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    private void checkAccessUser(UserDTO loggedUser, User recordsUser){
        if (!loggedUser.getId().equals(recordsUser.getId())){
            throw new AccessDeniedException(ExceptionCode.ACCESS_DENIED, "LOGGED USER IS DIFFERENT THAN USER RECORDS TO CHANGE");
        }
    }

    public OrderDTO deleteCurrentOrder(Long id, UserDTO loggedUser) {
        try {
            if (id == null) {
                throw new NullPointerException("ORDER ID IS NULL");
            }
            if (loggedUser == null) {
                throw new NullPointerException("LOGGED USER IS NULL");
            }
            Order orderToDelete = orderRepository.findById(id).orElseThrow(NullPointerException::new);
            checkAccessUser(loggedUser, orderToDelete.getUser());
            phoneOrderRepository.deleteAll(phoneOrderRepository.findAllByOrder(orderToDelete));
            orderRepository.delete(orderToDelete);
            return orderMapper.orderToOrderDTO(orderToDelete);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public PhoneOrderDTO deletePhoneOrder(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("PHONE ORDER IS NULL");
            }
            PhoneOrder phoneOrderToDelete = phoneOrderRepository.findById(id).orElseThrow(NullPointerException::new);
            phoneOrderRepository.delete(phoneOrderToDelete);
            return phoneOrderMapper.phoneOrderToPhoneOrderDTO(phoneOrderToDelete);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public PhoneOrderDTO changePhoneOrderQuantity(Integer quantity, Long phoneOrderId) {
        try {
            if (quantity == null) {
                throw new NullPointerException("QUANTITY IS NULL");
            }
            if (phoneOrderId == null) {
                throw new NullPointerException("PHONE ORDER ID IS NULL");
            }
            PhoneOrder phoneOrder = phoneOrderRepository.findById(phoneOrderId).orElseThrow(NullPointerException::new);
            phoneOrder.setQuantity(quantity);
            PhoneOrder phoneOrderFromDb = phoneOrderRepository.save(phoneOrder);
            return phoneOrderMapper.phoneOrderToPhoneOrderDTO(phoneOrderFromDb);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public InfoDTO realiseOrder(Long orderId, UserDTO loggedUser, HttpServletRequest request) {
        try {
            if (orderId == null) {
                throw new NullPointerException("ORDER ID IS NULL");
            }
            if (loggedUser == null) {
                throw new NullPointerException("LOGGED USER IS NULL");
            }

            Order order = orderRepository.findById(orderId).orElseThrow(NullPointerException::new);
            String phoneOrdersDesc = getPhoneOrdersDesc(phoneOrderRepository.findAllByOrder(order));

            final String url = "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
            eventPublisher.publishEvent(new OnConfirmationOrderEvenData(url, userMapper.userDTOToUser(loggedUser), phoneOrdersDesc));

            return InfoDTO.builder().info("Confirmation order mail has been sent").build();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public OrderDTO confirmOrder(String token, HttpServletRequest request) {
        try {
            if (token == null) {
                throw new NullPointerException("TOKEN IS NULL");
            }

            VerificationToken verificationToken
                    = verificationTokenRepository.findByToken(token)
                    .orElseThrow(() -> new NullPointerException("USER WITH TOKEN " + token + " DOESN'T EXIST"));

            if (verificationToken.getExpirationDateTime().isBefore(LocalDateTime.now())) {
                throw new NullPointerException("TOKEN HAS BEEN EXPIRED");
            }

            verificationToken.setToken(null);
            verificationTokenRepository.save(verificationToken);

            User user = verificationToken.getUser();

            Order order = orderRepository.findByUserId_EqualsAndIsCompletedFalse(user.getId()).orElseThrow(NullPointerException::new);
            Set<PhoneOrder> phoneOrders = phoneOrderRepository.findAllByOrder(order);
            String transactionNumber = UUID.randomUUID().toString();
            BigDecimal price = calculateOrderPrice(phoneOrders);
            String phoneOrdersDesc = getPhoneOrdersDesc(phoneOrders);
            String qrCodePath = qrCodeGeneratorService.generateQRCodeImage(transactionNumber);
            String amazonFilePath = amazonService.uploadFile(qrCodePath);

            final String url = "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
            eventPublisher.publishEvent(new OnCompletedOrderEvenData(url, user, phoneOrdersDesc, price, qrCodePath));

            order.setDateTime(LocalDateTime.now());
            order.setPrice(price);
            order.setIsCompleted(Boolean.TRUE);
            order.setTransactionNumber(transactionNumber);
            order.setQrCodePath(amazonFilePath);

            Order orderFromDb = orderRepository.save(order);
            return orderMapper.orderToOrderDTO(orderFromDb);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    private BigDecimal calculateOrderPrice(Set<PhoneOrder> phoneOrders){
        return phoneOrders
                .stream()
                .map(x -> x.getPhone().getPrice().multiply(new BigDecimal(x.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getPhoneOrdersDesc(Set<PhoneOrder> phoneOrders){
        return phoneOrders
                .stream()
                .map(x -> String.join(" ",
                        x.getPhone().getProducer(),
                        x.getPhone().getModel())
                )
                .collect(Collectors.joining(",  "));
    }
}
