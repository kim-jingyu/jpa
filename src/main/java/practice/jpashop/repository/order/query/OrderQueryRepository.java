package practice.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA에서 DTO 직접 조회
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    /**
     * 컬렉션 ( orderItems ) 별도로 조회
     * Query : 루트 1번, 컬렉션 N번
     * @return
     */
    public List<OrderQueryDto> getOrderQueryDtos() {
        // 루트 조회 ( toOne )
        List<OrderQueryDto> result = getOrderToOne();

        // 루프를 돌면서 컬렉션 추가 ( toMany )
        result.forEach(o-> {
            List<OrderItemQueryDto> orderItems = getOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    private List<OrderQueryDto> getOrderToOne() {
        return em.createQuery(
                "select new practice.jpashop.repository.order.query.OrderQueryDto(o.id, o.member.username, o.orderDate, o.status, o.delivery.address)" +
                        " from Orders o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class).getResultList();
    }

    private List<OrderItemQueryDto> getOrderItems(Long orderId) {
        return em.createQuery(
                "select new practice.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
