package jpabasic.jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.jpql.domain.Member;
import jpabasic.jpql.domain.Team;

import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            jpqlTest1(em);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }

    private static void jpqlTest1(EntityManager em) {
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
        member2.changeTeam(teamA);
        em.persist(member2);


        // JPQL 검색
        String jpql = "select m From Member m where m.age > 18";
        List<Member> result = em.createQuery(jpql, Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("멤버 = " + member);
        }
    }
}
