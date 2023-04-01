package practice.jpashop.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.jpashop.domain.Item;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    /**
     * 상품이 없으면, 신규 상품 저장
     * 상품이 있으면, 데이터베이스에 저장된 엔티티 수정
     * @param item
     */
    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }
    }

    /**
     * 상품 하나 찾기
     * @param id
     * @return 찾는 상품 하나
     */
    public Item findById(Long id) {
        return em.find(Item.class, id);
    }

    /**
     * 상품 모두 찾기
     * @return 상품 리스트
     */
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
