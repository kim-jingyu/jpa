package jpabasic.jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jpabasic.jpql.domain.*;

import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            init(em);

            persistenceContextInit(em);


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }

    private static void joinTest(EntityManager em) {
        String query = "select m from Member m right join m.team t on t.name = 'teamA'";
        List<Member> resultList = em.createQuery(query, Member.class)
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void pagingTest(EntityManager em) {
        List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        System.out.println("resultList.size() = " + resultList.size());
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    private static void forPagingTestInit(EntityManager em) {
        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            member.setUsername("member" + i);
            member.setAge(i);
            em.persist(member);
        }
    }

    private static void scalaTypeProjection2(EntityManager em) {
        List<MemberDTO> resultList = em.createQuery("select new jpabasic.jpql.domain.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                .getResultList();

        for (MemberDTO memberDTO : resultList) {
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
        }
    }

    private static void persistenceContextInit(EntityManager em) {
        em.flush();
        em.clear();
    }

    private static void scalaTypeProjection(EntityManager em) {
        List resultList = em.createQuery("select m.username, m.age from Member m")
                .getResultList();

        for (Object o : resultList) {
            Object[] result = (Object[]) o;
            System.out.println("username = " + result[0]);
            System.out.println("age = " + result[1]);
        }
    }

    private static void init(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("teamA");
        em.persist(teamA);

        Member member1 = new Member();
        member1.setUsername("member1");
        member1.setAge(20);
        member1.changeTeam(teamA);
        em.persist(member1);

        Member member2 = new Member();
        member2.setUsername("member2");
        member2.setAge(15);
        em.persist(member2);

        Address address = new Address();
        address.setCity("Seoul");
        address.setStreet("Gangnam");
        address.setZipcode("11111");

        Product product = new Product();
        product.setName("사과");
        product.setPrice(1000);
        product.setStockAmount(100);
        em.persist(product);

        Orders orders = new Orders();
        orders.setOrderAmount(1);
        orders.setAddress(address);
        orders.setMember(member1);
        orders.setProduct(product);
        em.persist(orders);
    }

    private static void nativeQueryTest1(EntityManager em) {
        // Query 가 실행되기 전에 flush 호출
        // Native SQL
        String sql = "select * from member where name = 'member1'";
        List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();

        for (Member member : resultList) {
            System.out.println("멤버 = " + member);
        }
    }

    private static void criteriaTest1(EntityManager em) {
        // Criteria 사용 준비
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);

        // Root Class (조회를 시작할 클래스)
        Root<Member> m = query.from(Member.class);

        // 쿼리 생성
        CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "member1"));
        List<Member> resultList = em.createQuery(cq).getResultList();

        for (Member member : resultList) {
            System.out.println("멤버 = " + member);
        }
    }

    private static void jpqlTest1(EntityManager em) {
        // JPQL 검색
        String jpql = "select m From Member m where m.age > 18";
        List<Member> result = em.createQuery(jpql, Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("멤버 = " + member);
        }
    }
}
