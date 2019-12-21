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
@Table(name = "profiles_parameters")
public class ProfileParameters {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer batteryBusiness;

    @Column(nullable = false)
    private Integer batteryEntertainment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Diagonals diagonalEntertainment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Rom romEntertainment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Ram ramEntertainment;
}





