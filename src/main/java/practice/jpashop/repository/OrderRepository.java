package practice.jpashop.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.jpashop.domain.Orders;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    /**
     * 주문 엔티티 저장
     * @param order
     */
    public void save(Orders order) {
        em.persist(order);
    }

    /**
     * 주문 엔티티 검색
     * @param id
     * @return 주문 엔티티 하나
     */
    public Orders findById(Long id) {
        return em.find(Orders.class, id);
    }

    // 주문 검색 기능
}
