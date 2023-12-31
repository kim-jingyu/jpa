package practice.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter @Setter
@Slf4j
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;     // 주문 금액
    private int count;          // 주문 수량

    public OrderItem() {
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", order=" + order +
                ", item=" + item +
                ", orderPrice=" + orderPrice +
                ", count=" + count +
                '}';
    }

    // 생성 메서드

    /**
     * 주문 상품, 가격, 수량 정보를 사용해서 주문상품 엔티티를 생성한다.
     * @param item
     * @param orderPrice
     * @param count
     * @return 주문상품 엔티티
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        log.info("createOrderItem 진입");
        log.info("파라미터 정보 item = {}, price = {}, count = {}", item, orderPrice, count);

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    // 비즈니스 로직

    /**
     * 주문 취소
     * 취소한 주문 수량만큼 상품의 재고를 증가시킨다.
     */
    public void cancel() {
        getItem().addStock(count);
    }

    // 조회 로직

    /**
     * 주문상품 전체 가격 조회
     * @return 주문 가격에 수량을 곱한 값
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
