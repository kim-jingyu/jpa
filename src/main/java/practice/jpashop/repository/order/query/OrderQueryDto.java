package practice.jpashop.repository.order.query;

import lombok.Data;
import practice.jpashop.domain.Address;
import practice.jpashop.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order 에 대한 쿼리 Dto 클래스
 */
@Data
public class OrderQueryDto {
    private Long orderId;
    private String username;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address deliveryAddress;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String username, LocalDateTime orderDate, OrderStatus orderStatus, Address deliveryAddress) {
        this.orderId = orderId;
        this.username = username;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.deliveryAddress = deliveryAddress;
    }
}
