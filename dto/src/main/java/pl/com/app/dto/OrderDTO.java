package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Boolean isCompleted;
    private String transactionNumber;
    private String qrCodePath;
    private LocalDateTime dateTime;
    private BigDecimal price;
    private UserDTO userDTO;
}





