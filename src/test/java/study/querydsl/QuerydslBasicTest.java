package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
@Commit
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory query;


    @BeforeEach
    void init() {
        query = new JPAQueryFactory(em);

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
    }

    @Test
    @DisplayName("user1을 찾아라 - jpql")
    void jpql1() {
        Member result = em.createQuery(
                        "select m from Member m" +
                                " where m.username = :username", Member.class)
                .setParameter("username", "user1")
                .getSingleResult();

        assertThat(result.getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("user1을 찾아라 - querydsl")
    void querydsl1() {
        QMember m = new QMember("m");

        Member user1 = query
                .select(m)
                .from(m)
                .where(m.username.eq("user1"))
                .fetchOne();

        assertThat(user1.getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("Q클래스 인스턴스를 사용하는 2가지 방법")
    void querydsl2() {
        Member user1 = query
                .selectFrom(member)
                .where(member.username.eq("user1"))
                .fetchOne();

        System.out.println("user1 = " + user1);
    }

    @Test
    @DisplayName("JPQL이 제공하는 모든 검색 조건 - 페이징에서 사용")
    void search1() {
        QueryResults<Member> results = query
                .selectFrom(member)
                .where(member.age.goe(10))
                .fetchResults();

        List<Member> content = results.getResults();

        for (Member contentMember : content) {
            System.out.println("contentMember = " + contentMember);
        }

        long total = results.getTotal();
        System.out.println("total = " + total);

        long offset = results.getOffset();
        System.out.println("offset = " + offset);

        long limit = results.getLimit();
        System.out.println("limit = " + limit);
    }

    @Test
    @DisplayName("JPQL이 제공하는 모든 검색 조건 - count 쿼리로 변경")
    void search2() {
        long fetchCount = query
                .selectFrom(member)
                .where(member.age.goe(10))
                .fetchCount();

        System.out.println("fetchCount = " + fetchCount);

    }

    @Test
    void joinTest1() {
        List<Member> members = query
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    /**
     * 세타 조인
     * 회원의 이름이 팀 이름과 같은 회원 조회
     * 연관관계 없는 회원 조회
     * cross join 이 일어남
     */
    @Test
    void theta_join() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> members = query
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }
}



























