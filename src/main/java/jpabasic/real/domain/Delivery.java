package jpabasic.real.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_ID")
    private Long id;
    @OneToOne(mappedBy = "delivery")
    private Order order;
    private String city;
    private String street;
    private String zipcode;
    @Enumerated
    private DeliveryStatus status;
}
