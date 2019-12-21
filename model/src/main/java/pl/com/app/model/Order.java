package pl.com.app.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Boolean isCompleted;

    private LocalDateTime dateTime;

    private BigDecimal price;

    private String transactionNumber;

    private String qrCodePath;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "order")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PhoneOrder> phoneOrders = new HashSet<>();

}





