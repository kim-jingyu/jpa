package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 스프링 데이터 JPA 테스트
 */
@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    void basicTest() {
        // 저장 기능 테스트
        Member user1 = new Member("user1", 10);
        memberRepository.save(user1);

        // 회원 ID로 찾기 테스트
        Member foundMember = memberRepository.findById(user1.getId()).get();
        assertThat(foundMember).isEqualTo(user1);

        // 모든 회원 찾기 테스트
        List<Member> allMembers = memberRepository.findAll();
        assertThat(allMembers).containsExactly(user1);

        // 회원 이름으로 찾기 테스트
        List<Member> foundMemberByName = memberRepository.findByUsername("user1");
        assertThat(foundMemberByName).containsExactly(user1);
    }

    @Test
    void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
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

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(15);
        condition.setAgeLoe(40);
        condition.setTeamName("teamA");

        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("user2");

    }
}