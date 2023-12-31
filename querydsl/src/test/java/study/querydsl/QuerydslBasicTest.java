package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
@Commit
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;
    @Autowired
    EntityManagerFactory emf;
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
    @DisplayName("정렬")
    void sorting() {
        em.persist(new Member(null, 100));
        em.persist(new Member("user5", 100));
        em.persist(new Member("user6", 100));

        List<Member> result = query
                .selectFrom(member)
                .where(member.age.loe(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member user5 = result.get(0);
        Member user6 = result.get(1);
        Member userNull = result.get(2);

        assertThat(user5.getUsername()).isEqualTo("user5");
        assertThat(user6.getUsername()).isEqualTo("user6");
        assertThat(userNull.getUsername()).isEqualTo(null);
    }

    @Test
    @DisplayName("페이징 - 조회 건수 제한")
    void paging() {
        List<Member> result = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)  // index 1부터 시작해서
                .limit(3)   // 최대 3건 조회
                .fetch();

        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("페이징 - 전체 조회 수")
    void paging2() {
        QueryResults<Member> queryResults = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(0)
                .limit(3)
                .fetchResults();

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getOffset()).isEqualTo(0);
        assertThat(queryResults.getLimit()).isEqualTo(3);
        assertThat(queryResults.getResults().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("집합 함수")
    void aggregation() {
        List<Tuple> result = query
                .select(member.count(),     // COUNT(m) 회원수
                        member.age.sum(),   // SUM(m.age) 나이 합
                        member.age.avg(),   // AVG(m.age) 평균 나이
                        member.age.max(),   // MAX(m.age) 최대 나이
                        member.age.min())   // MIN(m.age) 최소 나이
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);     // 회원수 = 4명
        assertThat(tuple.get(member.age.sum())).isEqualTo(100); // 나이합 = 100
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);  // 평균나이 = 25
        assertThat(tuple.get(member.age.max())).isEqualTo(40);  // 최대나이 = 40
        assertThat(tuple.get(member.age.min())).isEqualTo(10);  // 최소나이 = 10
    }

    @Test
    @DisplayName("팀의 이름과 각 팀의 평균 연령 구하기")
    void groupBy() {
        List<Tuple> result = query
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        System.out.println("teamA = " + teamA);
        System.out.println("teamB = " + teamB);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * 기본 조인
     * 팀 A에 소속된 모든 회원
     */
    @Test
    void defaultJoin1() {
        List<Member> result = query
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("user1", "user2");
    }

    /**
     * 세타 조인 - 연관관계가 없는 필드로 조인
     * 회원의 이름이 팀 이름과 같은 회원 조회 (연관관계가 없음)
     */
    @Test
    void thetaJoin() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = query
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result).extracting("username")
                .containsExactly("teamA", "teamB");
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

    /**
     * on 절을 활용한 조인
     * 1. 조인 대상 필터링
     * 2. 연관관계 없는 엔티티를 외부 조인한다.
     */

    // 1. 조인 대상 필터링
    @Test
    void ON_조인_필터링() {
        List<Tuple> result = query
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    // 2. 연관관계 없는 엔티티의 외부 조인
    @Test
    void ON_연관관계_없는_엔티티의_외부_조인() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> result = query
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))  // 서로 관계 없는 필드로 외부 조인
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    // fetch join 미적용
    @Test
    void fetch_join_미적용() {
        em.flush();
        em.clear();

        Member user1 = query
                .selectFrom(member)
                .where(member.username.eq("user1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(user1.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    // fetch join 적용
    @Test
    void fetch_join_적용() {
        em.flush();
        em.clear();

        Member user1 = query
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("user1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(user1.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

    // 서브 쿼리

    // 나이가 가장 많은 회원 조회
    @Test
    void subQuery() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = query
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(40);
    }

    // 나이가 평균 나이 이상인 회원
    @Test
    void subQueryGoe() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = query
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(30, 40);
    }

    // 서브쿼리 여러 건 처리, in 사용
    @Test
    void subQueryIn() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = query
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(20, 30, 40);
    }

    // select 절에 subQuery 넣기
    @Test
    void subQueryAtSelect() {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = query
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple.get(member.username) = " + tuple.get(member.username));
            System.out.println("tuple.get(JPAExpressions.select(memberSub.age.avg()).from(memberSub)) = " + tuple.get(select(memberSub.age.avg()).from(memberSub)));
        }
    }

    // static import 활용하기
    // import static com.querydsl.jpa.JPAExpressions.select;
    @Test
    void subQueryStaticImport() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = query
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();


    }

    // Case 문. select, 조건절(where), order by 에서 사용 가능

    // 단순한 조건
    @Test
    void case1() {
        List<String> result = query
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // 복잡한 조건
    @Test
    void case2() {
        List<String> result = query
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // orderBy 에서 case 문 함께 사용하기
    // rankPath 처럼 복잡한 조건을 변수로 선언하여 select 절, orderBy 절에서 사용할 수 있다.
    @Test
    void case3() {
        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = query
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.print("username = " + username);
            System.out.print(", age = " + age);
            System.out.println(", rank = " + rank);
        }
    }

    // 상수, 문자 더하기

    // 상수가 필요하면 Expressions.constant (xxx) 사용
    @Test
    void 상수() {
        List<Tuple> fetch = query
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    // 문자 더하기 concat
    @Test
    void 문자_더하기() {
        String result = query
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("user1"))
                .fetchOne();

        System.out.println("result = " + result);
    }

    // 프로젝션과 결과 반환

    // 프로젝션 대상이 하나일 때, 타입을 명확하게 지정할 수 있다.
    @Test
    void 프로젝션_대상_하나() {
        List<String> result = query
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // 프로젝션 대상이 둘 이상일 때, 튜플이나 DTO 로 조회할 수 있다.
    @Test
    void 프로젝션_대상_둘_이상_튜플() {
        List<Tuple> result = query
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }

    // 순수 JPA 에서 DTO 조회
    @Test
    void 순수_JPA_에서_DTO_조회() {
        List<MemberDto> result = em.createQuery(
                        "select new study.querydsl.dto.MemberDto(m.username, m.age) " +
                                "from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // Querydsl 빈 생성 ( Bean population )
    @Test
    @DisplayName("DTO 반환 - 프로퍼티 접근")
    void 프로퍼티_접근_Setter() {
        List<MemberDto> result = query
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("DTO 반환 - 필드 직접 접근")
    void 필드_직접_접근() {
        List<MemberDto> result = query
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("DTO 반환 - 별칭이 다를 때")
    void 별칭이_다를_때() {
        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = query
                .select(Projections.fields(UserDto.class,
                                member.username.as("name"),
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(memberSub.age.max())
                                                .from(memberSub), "age")
                        )
                ).from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    @DisplayName("DTO 반환 - 생성자 사용")
    void 생성자_사용() {
        List<MemberDto> result = query
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("@QueryProjection 활용")
    void queryProjection() {
        List<MemberDto> result = query
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("distinct")
    void distinct() {
        List<String> result = query
                .select(member.username).distinct()
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName("동적 쿼리 - BooleanBuilder 사용")
    void 동적쿼리_BooleanBuilder() {
        String usernameParam = "user1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);

        assertThat(result).extracting("username").containsExactly("user1");
    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }
        List<Member> result = query
                .selectFrom(member)
                .where(builder)
                .fetch();
        return result;
    }

    @Test
    @DisplayName("동적 쿼리 - Where 다중 파라미터 사용")
    void 동적쿼리_WhereParam() {
        String usernameParam = "user1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);

        assertThat(result).extracting("username").containsExactly("user1");
        assertThat(result).extracting("age").containsExactly(10);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return query
                .selectFrom(member)
                .where(allEq(usernameCond, ageCond))
                .fetch();
    }

    private Predicate allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    @Test
    @DisplayName("수정 벌크 연산")
    void bulkOperation() {
        // 쿼리 한번으로 대량 데이터 수정
        long nameCount = query
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        long ageCount = query
                .update(member)
                .set(member.age, member.age.multiply(2))
                .execute();

        em.flush();
        em.clear();

        List<Member> result = query
                .selectFrom(member)
                .fetch();

        assertThat(result).extracting("username").containsExactly("비회원", "비회원", "user3", "user4");
        assertThat(result).extracting("age").containsExactly(20, 40, 60, 80);
    }

    @Test
    @DisplayName("삭제 벌크 연산")
    void bulkOperation2() {
        long deleteCount = query
                .delete(member)
                .where(member.age.gt(18))
                .execute();

        em.flush();
        em.clear();

        List<Member> result = query
                .selectFrom(member)
                .fetch();

        assertThat(result).extracting("username").containsExactly("user1");
    }

    @Test
    @DisplayName("SQL function 호출하기")
    void sqlFunction() {
        // member -> M 으로 변경하는 replace 함수 사용
        List<String> result = query
                .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})", member.username, "user", "M"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }

        List<String> result2 = query
                .select(member.username)
                .from(member)
                .where(member.username.eq(Expressions.stringTemplate("function('upper', {0})", member.username)))
                .fetch();

        for (String s : result2) {
            System.out.println("s = " + s);
        }

        List<String> result3 = query
                .select(member.username.upper())
                .from(member)
                .fetch();

        for (String s : result3) {
            System.out.println("s = " + s);
        }
    }


}



























