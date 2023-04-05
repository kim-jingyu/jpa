package practice.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import practice.jpashop.domain.OrderSearch;
import practice.jpashop.domain.Orders;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 주문 검색 기능
     * 검색 조건에 동적으로 쿼리를 생성해서 주문 엔티티를 조회 ( JPQL )
     * @param orderSearch
     * @return 검색 조건에 따른 주문 엔티티 전체
     */
    public List<Orders> findAll(OrderSearch orderSearch) {
        String jpql = "select o from Orders o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.username like :name";
        }

        TypedQuery<Orders> query = em.createQuery(jpql, Orders.class)
                .setMaxResults(1000);// 최대 1000건 검색

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * 주문 검색 기능
     * JPA Criteria 로 처리
     * @param orderSearch
     * @return 검색 조건에 따른 주문 엔티티 전체
     */
    public List<Orders> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Orders> cq = cb.createQuery(Orders.class);
        Root<Orders> o = cq.from(Orders.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);  // 회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Orders> query = em.createQuery(cq).setMaxResults(1000);      // 최대 1000건
        return query.getResultList();
    }

    public List<Orders> findAllWithMemberDelivery() {
        return em.createQuery("select o from Orders o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Orders.class
        ).getResultList();
    }
}
