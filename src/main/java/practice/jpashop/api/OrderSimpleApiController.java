package practice.jpashop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.jpashop.domain.OrderSearch;
import practice.jpashop.domain.Orders;
import practice.jpashop.repository.OrderRepository;

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

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
