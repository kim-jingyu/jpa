package jpabasic.real;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.real.domain.*;
import jpabasic.real.domain.item.Album;
import jpabasic.real.domain.item.Item;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static jpabasic.real.domain.OrderStatus.*;

@Slf4j
public class RealMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            Delivery delivery = new Delivery();
            delivery.setStatus(DeliveryStatus.COMPLETE);
            em.persist(delivery);

            Order order1 = new Order();
            order1.setStatus(COMPLETE);
            order1.setMember(member1);
            order1.setDelivery(delivery);
            em.persist(order1);

            Album album = new Album();
            album.setName("치즈버거");
            album.setArtist("맥도날드");
            album.setStockQuantity(99);
            em.persist(album);

            Category category = new Category();
            category.setName("햄버거");
            category.setItems(getItem(album));
            em.persist(category);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderPrice(8000);
            orderItem.setCount(1);
            orderItem.setItem(album);
            orderItem.setOrder(order1);
            em.persist(orderItem);

            // 역방향 참조
            member1.getOrders().add(order1);
            order1.getOrderItems().add(orderItem);

            String query = "select i from Item i where type(i) in (Album, Movie)";
            List<Item> resultList = em.createQuery(query, Item.class)
                    .getResultList();

            for (Item item : resultList) {
                System.out.println("item = " + item);
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void findTest(EntityManager em, Member member1, Order order1) {
        Member foundMember = em.find(Member.class, member1.getId());
        log.info("오더 개수 = {}", foundMember.getOrders().size());
        Order foundOrder = em.find(Order.class, order1.getId());
        log.info("오더 아이템 개수 = {}", foundOrder.getOrderItems().size());
    }

    private static List<Item> getItem(Item itemA) {
        List<Item> items = new ArrayList<>();
        items.add(itemA);
        return items;
    }
}
