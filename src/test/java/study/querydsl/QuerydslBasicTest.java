package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
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
    public void jpql1() {
        Member result = em.createQuery(
                        "select m from Member m" +
                                " where m.username = :username", Member.class)
                .setParameter("username", "user1")
                .getSingleResult();

        assertThat(result.getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("user1을 찾아라 - querydsl")
    public void querydsl1() {
        QMember m = new QMember("m");

        Member user1 = query
                .select(m)
                .from(m)
                .where(m.username.eq("user1"))
                .fetchOne();

        assertThat(user1.getUsername()).isEqualTo("user1");
    }



}
