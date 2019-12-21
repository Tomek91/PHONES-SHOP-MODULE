package pl.com.app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import pl.com.app.model.enums.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneDTO {
    private Long id;
    private String producer;
    private String model;
    private BigDecimal price;
    private Integer quantity;
    private Diagonals diagonal;
    private OperatingSystem operatingSystem;
    private Resolution resolution;
    private Rom rom;
    private Ram ram;
    private DataCommunication dataCommunication;
    private Boolean isDualSim;
    private String color;
    private Screen screen;
    private Integer capacity;
    private Boolean isBluetooth;
    private Boolean isWifi;
    private Boolean isJack;
    private Boolean isUSB;
    private MemoryCard memoryCard;
    private String imgPath;
    private MultipartFile file;
}





