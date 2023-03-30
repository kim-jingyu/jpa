package jpabasic.jpql.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue
    private Long id;
    private int orderAmount;
    @Embedded
    private Address address;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    public Orders() {

    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", orderAmount=" + orderAmount +
                ", address=" + address +
                '}';
    }
}
