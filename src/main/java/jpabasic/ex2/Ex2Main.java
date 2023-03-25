package jpabasic.ex2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.ex2.domain.Member;
import jpabasic.ex2.domain.Team;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Ex2Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            lazyLoadingEagerLoading2(em);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void lazyLoadingEagerLoading2(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("RealMadrid");
        em.persist(teamA);

        Team teamB = new Team();
        teamB.setName("Barcelona");
        em.persist(teamB);

        Member member1 = new Member();
        member1.setUserName("Modric");
        member1.setTeam(teamA);
        em.persist(member1);

        Member member2 = new Member();
        member2.setUserName("Lewandowski");
        member2.setTeam(teamA);
        em.persist(member2);

        em.flush();
        em.clear();

        // SQL : select * from Member
        // SQL : select * from Team where TEAM_ID = member.team_id
        List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class)
                .getResultList();
    }

    /**
     * 지연로딩, 즉시로딩
     */
    private static void lazyLoadingEagerLoading1(EntityManager em) {
        Team team = new Team();
        team.setName("RealMadrid");
        em.persist(team);

        Member member = new Member();
        member.setUserName("Modric");
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        Member member1 = em.find(Member.class, member.getId());

        System.out.println("member1.getTeam().getClass() = " + member1.getTeam().getClass()); // 프록시 객체 찾기

        System.out.println("=================");
        member1.getTeam().getName();
        System.out.println("=================");
    }

    /**
     * 연관관계 주인
     */
    private static void findRelationalOwner(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("teamA");
        em.persist(teamA);

        Member member1 = new Member();
        member1.setUserName("member1");

        Member member2 = new Member();
        member2.setUserName("member2");

        // 연과관계 역방향 - 설정
        teamA.getMembers().add(member1);
        teamA.getMembers().add(member2);

        // 연관관계 주인 - 설정
        member1.setTeam(teamA);
        em.persist(member1);
        member2.setTeam(teamA);
        em.persist(member2);

        Team foundTeam = em.find(Team.class, teamA.getId());
        int memberSize = foundTeam.getMembers().size();
        log.info("memberSize = {}", memberSize);
    }

    private static void test1(EntityManager em) {
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

        Member newMember = new Member();
        newMember.setTeam(teamB);
        em.persist(newMember);

        // member1 에 새로운 teamB 설정, 연관관계 수정
        member.setTeam(teamB);
        Team newFoundTeam = foundMember.getTeam();
        log.info("newFoundTeam.getName() = {}", newFoundTeam.getName());

        // 양방향 매핑, 반대 반향으로 객체 그래프 탐색
        Team foundTeamB = em.find(Team.class, teamB.getId());
        int memberSize = foundTeamB.getMembers().size();            // 역방향 조회
        log.info("memberSize = {}", memberSize);
    }


}
