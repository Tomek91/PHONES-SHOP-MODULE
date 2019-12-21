package pl.com.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.app.model.enums.Diagonals;
import pl.com.app.model.enums.Ram;
import pl.com.app.model.enums.Rom;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer batteryThreshold;

    @Enumerated(EnumType.STRING)
    private Diagonals diagonalThreshold;

    private Boolean isWifiThreshold;

    private Boolean isJackThreshold;

    private Boolean isUSBThreshold;

    @Enumerated(EnumType.STRING)
    private Rom romThreshold;

    @Enumerated(EnumType.STRING)
    private Ram ramThreshold;
}





