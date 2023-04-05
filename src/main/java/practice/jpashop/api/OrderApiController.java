package practice.jpashop.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.jpashop.domain.OrderItem;
import practice.jpashop.domain.OrderSearch;
import practice.jpashop.domain.Orders;
import practice.jpashop.repository.OrderRepository;

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

    @Data
    static class Result<T> {
        private T data;
    }
}
