package practice.jpashop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpashop.domain.*;
import practice.jpashop.repository.ItemRepository;
import practice.jpashop.repository.MemberRepository;
import practice.jpashop.repository.OrderRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findById(memberId);
        Item item = itemRepository.findById(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // 주문 생성
        Orders order = Orders.createOrder(member, delivery, orderItem);
        log.info("order 정보 = {}", order.getOrderItems());

        // 주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {

        // 주문 엔티티 조회
        Orders order = orderRepository.findById(orderId);

        // 주문 취소
        order.cancel();
    }

    // 주문 검색
    public List<Orders> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }
}
