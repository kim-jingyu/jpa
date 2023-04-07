package practice.jpashop.repository.order.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import practice.jpashop.domain.Address;
import practice.jpashop.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order 에 대한 쿼리 Dto 클래스
 */
@Data
// V6. JPA에서 DTO로 직접 조회시, 플랫 데이터의 최적화에서 group by 할 때 다른 객체이기에 묶어야 한다.
// 이때, orderId 로 뻥튀기된 row 들을 기준점으로 하여 묶어준다.
@EqualsAndHashCode(of = "orderId")
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

    public OrderQueryDto(Long orderId, String username, LocalDateTime orderDate, OrderStatus orderStatus, Address deliveryAddress, List<OrderItemQueryDto> orderItems) {
        this.orderId = orderId;
        this.username = username;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.deliveryAddress = deliveryAddress;
        this.orderItems = orderItems;
    }
}
