package pl.com.app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneOrderAddDTO {
    private PhoneDTO phoneDTO;
    private Integer quantity;
}
