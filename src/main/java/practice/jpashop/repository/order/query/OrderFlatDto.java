package practice.jpashop.repository.order.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import practice.jpashop.domain.Address;
import practice.jpashop.domain.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderFlatDto {
    private Long orderId;
    private String username;
    private LocalDateTime orderDate;
    private Address deliveryAddress;
    private OrderStatus orderStatus;

    private String itemName;
    private int orderPrice;
    private int count;

    public OrderFlatDto(Long orderId, String username, LocalDateTime orderDate, Address deliveryAddress, OrderStatus orderStatus, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.username = username;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.orderStatus = orderStatus;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
