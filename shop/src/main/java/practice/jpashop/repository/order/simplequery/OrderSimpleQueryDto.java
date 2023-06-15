package practice.jpashop.repository.order.simplequery;

import lombok.Data;
import practice.jpashop.domain.Address;
import practice.jpashop.domain.OrderStatus;
import practice.jpashop.domain.Orders;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String userName;
    private LocalDateTime orderDate;    // 주문시간
    private OrderStatus orderStatus;
    private Address deliveryAddress;

    public OrderSimpleQueryDto(Long orderId, String userName, LocalDateTime orderDate, OrderStatus orderStatus, Address deliveryAddress) {
        this.orderId = orderId;
        this.userName = userName;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.deliveryAddress = deliveryAddress;
    }
}
