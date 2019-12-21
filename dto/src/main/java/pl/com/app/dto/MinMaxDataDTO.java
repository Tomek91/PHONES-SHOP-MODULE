package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.app.model.enums.*;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinMaxDataDTO {
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private Diagonals maxDiagonal;
    private Diagonals minDiagonal;
    private Resolution maxResolution;
    private Resolution minResolution;
    private Rom maxRom;
    private Rom minRom;
    private Ram maxRam;
    private Ram minRam;
    private DataCommunication maxDataCommunication;
    private DataCommunication minDataCommunication;
    private Boolean isDualSim;
    private Screen maxScreen;
    private Screen minScreen;
    private Integer maxCapacity;
    private Integer minCapacity;
    private Boolean isBluetooth;
    private Boolean isWifi;
    private Boolean isJack;
    private Boolean isUSB;
    private MemoryCard maxMemoryCard;
    private MemoryCard minMemoryCard;
}
