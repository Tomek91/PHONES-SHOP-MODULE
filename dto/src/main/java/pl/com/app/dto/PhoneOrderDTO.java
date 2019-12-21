package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneOrderDTO {
    private Long id;
    private Integer quantity;
    private PhoneDTO phoneDTO;
    private OrderDTO orderDTO;
}





