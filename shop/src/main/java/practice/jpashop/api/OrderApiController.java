package practice.jpashop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import practice.jpashop.domain.*;
import practice.jpashop.repository.OrderRepository;
import practice.jpashop.repository.order.query.OrderFlatDto;
import practice.jpashop.repository.order.query.OrderItemQueryDto;
import practice.jpashop.repository.order.query.OrderQueryDto;
import practice.jpashop.repository.order.query.OrderQueryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 컬렉션 조회 최적화
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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

    @GetMapping("/api/v3.1/orders")
    public Result ordersListV3_paging(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "100") int limit) {
        List<Orders> ordersList = orderRepository.findAllWithPaging(offset, limit);
        List<OrderDto> dtos = ordersList.stream().map(OrderDto::new).toList();
        return new Result(dtos);
    }

    @GetMapping("/api/v4/orders")
    public Result ordersListV4() {
        return new Result(orderQueryRepository.getOrderQueryDtos());
    }

    @GetMapping("/api/v5/orders")
    public Result ordersListV5() {
        return new Result(orderQueryRepository.getOrderQueryDtos_optimization());
    }

    @GetMapping("/api/v6/orders")
    public Result ordersListV6() {
//        return new Result(orderQueryRepository.findAllByDto_flat());
        List<OrderFlatDto> flatDtos = orderQueryRepository.findAllByDto_flat();
//        return new Result(flatDtos);
//        OrderQueryDto
//        private Long orderId;
//        private String username;
//        private LocalDateTime orderDate;
//        private OrderStatus orderStatus;
//        private Address deliveryAddress;
//        private List<OrderItemQueryDto> orderItems;

//        OrderItemQueryDto
//        private Long orderId;       // 주문번호
//        private String itemName;    // 상품명
//        private int orderPrice;     // 주문가격
//        private int count;          // 주문수량

        return new Result(flatDtos.stream()
                .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getUsername(), o.getOrderDate(), o.getOrderStatus(), o.getDeliveryAddress()),
                        Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getUsername(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getDeliveryAddress(), e.getValue()))
                .toList());
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
