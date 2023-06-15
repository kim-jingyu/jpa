package practice.jpashop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.jpashop.domain.Address;
import practice.jpashop.domain.OrderSearch;
import practice.jpashop.domain.OrderStatus;
import practice.jpashop.domain.Orders;
import practice.jpashop.repository.OrderRepository;
import practice.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import practice.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ~ToOne 관계 최적화
 * Orders -> Member
 * Orders -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * 엔티티 직접 노출
     * @return 주문 리스트
     */
    @GetMapping("/api/v1/simple-orders")
    public Result ordersListV1() {
        List<Orders> orderList = orderRepository.findAll(new OrderSearch());

        // 강제 지연 로딩 설정 대신에
        for (Orders order : orderList) {
            order.getMember().getUsername();    // LAZY 강제 초기화
            order.getDelivery().getAddress();   // LAZY 강제 초기화
        }

        return new Result(orderList);
    }

    @GetMapping("/api/v2/simple-orders")
    public Result ordersListV2() {
        List<Orders> ordersList = orderRepository.findAll(new OrderSearch());

        List<SimpleOrderDto> dtos = ordersList.stream()
                .map(SimpleOrderDto::new)
                .toList();

        return new Result(ordersList);
    }

    @GetMapping("/api/v3/simple-orders")
    public Result ordersListV3() {
        List<Orders> ordersList = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> dtos = ordersList.stream()
                .map(SimpleOrderDto::new)
                .toList();

        return new Result(dtos);
    }

    @GetMapping("/api/v4/simple-orders")
    public Result ordersListV4() {
        return new Result(orderSimpleQueryRepository.findOrderDtos());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String userName;
        private LocalDateTime orderDate;    // 주문시간
        private OrderStatus orderStatus;
        private Address deliveryAddress;

        public SimpleOrderDto(Orders o) {
            orderId = o.getId();
            userName = o.getMember().getUsername();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            deliveryAddress = o.getDelivery().getAddress();
        }
    }
}
