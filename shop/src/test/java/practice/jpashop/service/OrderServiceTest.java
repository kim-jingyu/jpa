package practice.jpashop.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import practice.jpashop.domain.*;
import practice.jpashop.exception.NotEnoughStockException;
import practice.jpashop.repository.OrderRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문 테스트")
    public void 상품_주문() {
        // Given
        Member member = createMember();
        Item item = createMovie();
        int orderCount = 2;

        // When
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // Then
        Orders foundOrder = orderRepository.findById(orderId);

        assertEquals(OrderStatus.ORDER, foundOrder.getStatus(), "상품 주문시 상태는 ORDER 이어야 한다.");
        assertEquals(1, foundOrder.getOrderItems().size(), "주문한 상품 종류의 수가 정확해야 한다.");
        assertEquals(10000 * 2, foundOrder.getTotalPrice(), "주문 가격은 가격 X 수량이다.");
        assertEquals(8, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    @Test
    @DisplayName("상품주문 재고수량 초과 테스트")
    public void 상품주문_재고수량_초과() {
        // Given
        Member member = createMember();
        Item item = createMovie();
        int orderCount = 11; // 재고보다 많은 수량

        // When
        // Then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> orderService.order(member.getId(), item.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class);
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void 주문_취소() {
        // Given
        Member member = createMember();
        Item item = createMovie();
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // When
        orderService.cancelOrder(orderId);

        // Then
        Orders foundOrder = orderRepository.findById(orderId);

        assertEquals(OrderStatus.CANCEL, foundOrder.getStatus(), "주문 취소시 상태는 CANCEL 이어야 한다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");
    }

    private Movie createMovie() {
        Movie movie = new Movie();
        movie.setName("존 윅");
        movie.setStockQuantity(10);
        movie.setPrice(10000);
        em.persist(movie);
        return movie;
    }

    private Member createMember() {
        Member member = new Member();
        member.setUsername("user");
        member.setAddress(new Address("서울시", "강남구", "11111"));
        em.persist(member);
        return member;
    }
}