package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("순수 JPA 리포지토리 테스트")
    void basicTest() {
        // 저장 기능 테스트
        Member user1 = new Member("user1", 10);
        memberJpaRepository.save(user1);

        // 회원 ID로 찾기 테스트
        Member foundMember = memberJpaRepository.findById(user1.getId()).get();
        assertThat(foundMember).isEqualTo(user1);

        // 모든 회원 찾기 테스트
        List<Member> allMembers = memberJpaRepository.findAll();
        assertThat(allMembers).containsExactly(user1);

        // 회원 이름으로 찾기 테스트
        List<Member> foundMemberByName = memberJpaRepository.findByUsername("user1");
        assertThat(foundMemberByName).containsExactly(user1);
    }

    @Test
    @DisplayName("querydsl 테스트")
    void querydslTest() {
        // 저장 기능 테스트
        Member user1 = new Member("user1", 10);
        memberJpaRepository.save(user1);

        // 회원 ID로 찾기 테스트
        Member foundMember = memberJpaRepository.findById(user1.getId()).get();
        assertThat(foundMember).isEqualTo(user1);

        // 모든 회원 찾기 테스트
        List<Member> allMembers = memberJpaRepository.findAll_querydsl();
        assertThat(allMembers).containsExactly(user1);

        // 회원 이름으로 찾기 테스트
        List<Member> foundMemberByName = memberJpaRepository.findByUsername_querydsl("user1");
        assertThat(foundMemberByName).containsExactly(user1);
    }

    @Test
    @DisplayName("동적 쿼리 - Builder 사용")
    void 동적쿼리_Builder() {
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

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);

        assertThat(result).extracting("username").containsExactly("user2");

    }
}