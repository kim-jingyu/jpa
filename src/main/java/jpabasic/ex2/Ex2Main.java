package jpabasic.ex2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.ex2.domain.Member;
import jpabasic.ex2.domain.Team;

public class Ex2Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // 팀 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            // 회원 저장
            Member member = new Member();
            member.setUsername("member1");
            member.setTeamId(team.getId());
            em.persist(member);

            // 식별자로 다시 회원 조회, 객체 지향적이지 않음.
            Member foundMember = em.find(Member.class, member.getId());

            // 연관관계가 없음
            Team foundTeam = em.find(Team.class, team.getId());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
