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
public class ProfileDTO {
    private Long id;
    private String name;
    private Integer batteryThreshold;
    private Diagonals diagonalThreshold;
    private Boolean isWifiThreshold;
    private Boolean isJackThreshold;
    private Boolean isUSBThreshold;
    private Rom romThreshold;
    private Ram ramThreshold;
}


