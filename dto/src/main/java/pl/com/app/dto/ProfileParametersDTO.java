package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.app.model.enums.Diagonals;
import pl.com.app.model.enums.Ram;
import pl.com.app.model.enums.Rom;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileParametersDTO {
    private Long id;
    private Integer batteryBusiness;
    private Integer batteryEntertainment;
    private Diagonals diagonalEntertainment;
    private Rom romEntertainment;
    private Ram ramEntertainment;
}
