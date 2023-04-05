package practice.jpashop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.jpashop.domain.*;
import practice.jpashop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Orders> ordersListV1() {
        List<Orders> ordersList = orderRepository.findAll(new OrderSearch());
        for (Orders order : ordersList) {
            order.getMember().getUsername();    // Lazy 강제 초기화
            order.getDelivery().getAddress();   // Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(oi -> oi.getItem().getName());
        }
        return ordersList;
    }

    // 엔티티 -> DTO
    @GetMapping("/api/v2/orders")
    public Result ordersListV2() {
        List<Orders> ordersList = orderRepository.findAll(new OrderSearch());
        List<OrderDto> orderDtos = ordersList.stream()
                .map(OrderDto::new)
                .toList();
        return new Result(orderDtos);
    }

    @GetMapping("/api/v3/orders")
    public Result ordersListV3() {
        List<Orders> ordersList = orderRepository.findAllWithOrderItem();
        List<OrderDto> orderDtos = ordersList.stream().map(OrderDto::new).toList();
        return new Result(orderDtos);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String userName;
        private LocalDateTime orderDate;    // 주문시간
        private OrderStatus orderStatus;
        private Address deliveryAddress;
        private List<OrderItemDto> orderItems;

        public OrderDto(Orders o) {
            orderId = o.getId();
            userName = o.getMember().getUsername();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            deliveryAddress = o.getDelivery().getAddress();
            orderItems = o.getOrderItems().stream().map(OrderItemDto::new).toList();
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;    // 상품명
        private int orderPrice;     // 상품가격
        private int count;          // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
