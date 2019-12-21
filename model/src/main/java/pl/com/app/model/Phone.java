package pl.com.app.model;

import lombok.*;
import pl.com.app.model.enums.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "phones")
public class Phone {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String producer;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private BigDecimal price;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private Diagonals diagonal;

    @Enumerated(EnumType.STRING)
    private OperatingSystem operatingSystem;

    @Enumerated(EnumType.STRING)
    private Resolution resolution;

    @Enumerated(EnumType.STRING)
    private Rom rom;

    @Enumerated(EnumType.STRING)
    private Ram ram;

    @Enumerated(EnumType.STRING)
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

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "phone")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PhoneOrder> phoneOrders = new HashSet<>();
}





