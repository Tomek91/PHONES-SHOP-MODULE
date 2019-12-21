package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.app.model.enums.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhonesFilterDataDTO {
    private List<PhoneDTO> phoneDTOList;
    private SortDataDTO sortDataDTO;
    private BigDecimal price;
    private Integer quantity;
    private List<Diagonals> diagonals;
    private List<OperatingSystem> operatingSystems;
    private List<Resolution> resolutions;
    private List<Rom> roms;
    private List<Ram> rams;
    private List<DataCommunication> dataCommunications;
    private Boolean isDualSim;
    private String color;
    private List<Screen> screens;
    private Integer capacity;
    private Boolean isBluetooth;
    private Boolean isWifi;
    private Boolean isJack;
    private Boolean isUSB;
    private List<MemoryCard> memoryCards;
}
