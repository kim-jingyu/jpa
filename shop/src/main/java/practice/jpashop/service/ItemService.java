package practice.jpashop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpashop.domain.Item;
import practice.jpashop.repository.ItemRepository;

import java.util.List;

/**
 * 상품 서비스는 상품 리포지토리에 단순 위임하는 클래스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    /**
     * 영속성 컨텍스트가 자동 변경하게 한다. (변경 감지)
     * 식별자와 변경할 데이터를 명확하게 전달한다. (파라미터 or dto)
     */
    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity) {
        Item item = itemRepository.findById(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }
}
