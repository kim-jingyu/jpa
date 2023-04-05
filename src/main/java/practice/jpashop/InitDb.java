package practice.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.jpashop.domain.*;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.init1();
        initService.init2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;

        public void init1() {
            Member member = createMember("사용자1", "서울시", "강남구", "11111");
            em.persist(member);

            Movie movie1 = createMovie("John Wick 1", 10000, 100);
            em.persist(movie1);

            Movie movie2 = createMovie("John Wick 2", 11000, 110);
            em.persist(movie2);

            OrderItem orderItem1 = OrderItem.createOrderItem(movie1, movie1.getPrice(), 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(movie2, movie2.getPrice(), 2);
            Orders order = Orders.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order);
        }

        public void init2() {
            Member member = createMember("사용자2", "부산시", "서면구", "22222");
            em.persist(member);

            Movie movie1 = createMovie("Avatar 1", 12000, 120);
            em.persist(movie1);

            Movie movie2 = createMovie("Avatar 2", 13000, 130);
            em.persist(movie2);

            OrderItem orderItem1 = OrderItem.createOrderItem(movie1, movie1.getPrice(), 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(movie2, movie2.getPrice(), 4);
            Orders order = Orders.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order);
        }

        private Member createMember(String username, String city, String street, String zipcode) {
            Member member = new Member();
            member.setUsername(username);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Movie createMovie(String name, int price, int stockQuantity) {
            Movie movie = new Movie();
            movie.setName(name);
            movie.setPrice(price);
            movie.setStockQuantity(stockQuantity);
            return movie;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
