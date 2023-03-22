package jpabasic.ex2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.ex2.domain.Member;
import jpabasic.ex2.domain.Team;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ex2Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // 팀 저장
            Team teamA = new Team();
            teamA.setName("TeamA");
            em.persist(teamA);

            // 회원 저장
            Member member = new Member();
            member.setUserName("member1");
            member.setTeam(teamA);           // 단방향 연관관계 설정, 참조 저장
            em.persist(member);

            // 식별자로 회원 조회
            Member foundMember = em.find(Member.class, member.getId());

            // 연관관계가 없음, 객체 지향적이지 않음.
//            Team foundTeam = em.find(Team.class, team.getId());

            // 참조를 사용해서 연관관계 조회
            Team foundTeam = foundMember.getTeam();
            log.info("foundTeam.getName() = {}", foundTeam.getName());

            // 새로운 팀 B
            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            // member1 에 새로운 teamB 설정, 연관관계 수정
            member.setTeam(teamB);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
