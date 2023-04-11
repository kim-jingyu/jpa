package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("TeamB");
        em.persist(teamA);
        em.persist(teamB);

        Member user1 = new Member("user1", 10, teamA);
        Member user2 = new Member("user2", 20, teamA);
        Member user3 = new Member("user3", 30, teamB);
        Member user4 = new Member("user4", 40, teamB);

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);

        // 초기화
        em.flush();
        em.clear();

        // 확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        }
    }

    @Test
    @DisplayName("Auditing - 순수 JPA")
    void auditing() throws InterruptedException {
        Member userA = new Member("userA");
        memberRepository.save(userA);  // @PrePersist

        Thread.sleep(200);
        userA.setUsername("userB");

        em.flush(); // @PreUpdate
        em.clear();

        Member foundUserA = memberRepository.findById(userA.getId()).get();

        System.out.println("foundUserA.getCreatedDate() = " + foundUserA.getCreatedDate());
        System.out.println("foundUserA.getUpdatedDate() = " + foundUserA.getLastModifiedDate());
        System.out.println("foundUserA.getCreatedBy() = " + foundUserA.getCreatedBy());
        System.out.println("foundUserA.getLastModifiedBy() = " + foundUserA.getLastModifiedBy());
    }
}