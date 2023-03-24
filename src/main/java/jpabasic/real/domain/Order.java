package jpabasic.real.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDER_TABLE")
@Getter
@Setter
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDateTime orderDate;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @OneToOne
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;

    /**
     * 연관관계 편의 메서드
     * 양방향 연관 관계
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public Order() {
    }
}
