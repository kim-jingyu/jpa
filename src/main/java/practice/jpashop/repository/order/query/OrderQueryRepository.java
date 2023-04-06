package practice.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 단건 조회에서 많이 사용하는 방식
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

    /**
     * 최적화
     * Query -> 루트 1번, 컬렉션 1번
     * 데이터를 한번에 처리할 때 많이 사용하는 방식
     * ~ToOne 관계들을 먼저 조회하고, 여기서 얻은 식별자인 orderId로 ~ToMany 관계인 OrderItem 을 한꺼번에 조회한다.
     * Map 을 사용해서 매칭 성능 향상시킬 수 있다. ( O(1) )
     * @return
     */
    public List<OrderQueryDto> getOrderQueryDtos_optimization() {
        // 루트 조회 ( toOne )
        List<OrderQueryDto> result = getOrderToOne();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = getOrderItemMap(getOrderIds(result));

        // 루프를 돌면서 orderItems 를 result 에 추가 ( 추가 쿼리가 실행되지 않음 )
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> getOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new practice.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderId()));
    }

    private List<Long> getOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(OrderQueryDto::getOrderId)
                .toList();
    }
}
