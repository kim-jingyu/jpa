package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.MemberSpec;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    void testMember() {
        Member member = new Member("userA");

        Member savedMember = memberRepository.save(member);

        Member foundMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
        assertThat(foundMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(foundMember).isEqualTo(savedMember);
    }

    @Test
    @DisplayName("스프링 데이터 JPA 인터페이스 구현 클래스 확인")
    void 공통_인터페이스_설정() {
        log.info("스프링 데이터 JPA 인터페이스 구현 클래스 = {}", memberRepository.getClass());
    }

    @Test
    @DisplayName("기본 CRUD 테스트")
    void basicCRUD() {
        Member userA = new Member("userA");
        Member userB = new Member("userB");
        memberRepository.save(userA);
        memberRepository.save(userB);

        // 단건 조회 검증
        Member foundUserA = memberRepository.findById(userA.getId()).get();
        Member foundUserB = memberRepository.findById(userB.getId()).get();

        // 리스트 조회 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(userA);
        memberRepository.delete(userB);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    @DisplayName("같은 이름의 회원 중 나이가 설정한 값 이상인 회원 조회 테스트")
    void findByUsernameAndAgeGreaterThan() {
        Member user1 = new Member("userA", 10);
        Member user2 = new Member("userA", 20);
        memberRepository.save(user1);
        memberRepository.save(user2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("userA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("userA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("이름 부분 검색 테스트")
    void findHelloByUsernameLikeAndAgeNotNull() {
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB");
        memberRepository.save(userA);
        memberRepository.save(userB);

        List<Member> result = memberRepository.findHelloByUsernameContaining("user");

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("@Query, 리포지토리 메서드에 쿼리 정의하기")
    void 리포지토리_메서드에_쿼리_정의하기() {
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberRepository.save(userA);
        memberRepository.save(userB);

        List<Member> result = memberRepository.findUser("userA", 10);

        assertThat(result.get(0)).isEqualTo(userA);
    }

    @Test
    @DisplayName("@Query, 단순 값 하나 조회")
    void 단순_값_하나_조회() {
        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        memberRepository.save(userA);
        memberRepository.save(userB);

        List<String> usernameList = memberRepository.findUsernameList();

        for (String username : usernameList) {
            System.out.println("username = " + username);
        }
    }

    @Test
    @DisplayName("@Query, DTO 조회하기")
    void DTO_조회하기() {
        Team team = new Team("team1");
        teamRepository.save(team);

        Member userA = new Member("userA", 10);
        Member userB = new Member("userB", 20);
        userA.changeTeam(team);
        userB.changeTeam(team);
        memberRepository.save(userA);
        memberRepository.save(userB);

        List<MemberDto> dtoList = memberRepository.findMemberDto();

        for (MemberDto memberDto : dtoList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("페이징 조건과 정렬 조건 설정")
    void 페이징_조건과_정렬_조건_설정() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> result = memberRepository.findPageByAge(10, pageRequest);
        Page<MemberDto> dtoPage = result.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        Sort sort = result.getPageable().getSort();
        sort.stream().iterator().forEachRemaining(s -> System.out.println("s = " + s));

        List<Member> content = result.getContent(); // 조회된 데이터
        assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        assertThat(result.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        assertThat(result.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(result.getTotalPages()).isEqualTo(2); // 전체 페이지 수
        assertThat(result.isFirst()).isTrue(); // 첫번째 항목인가?
        assertThat(result.hasNext()).isTrue(); // 다음 페이지가 있는가?
    }

    @Test
    @DisplayName("페이징 Slice")
    void 페이징_Slice() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Slice<Member> result = memberRepository.findSliceByAge(10, pageRequest);

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @DisplayName("페이징 리스트")
    void 페이징_리스트() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        List<Member> result = memberRepository.findListByAge(10, pageRequest);

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @DisplayName("페이징 Top,First")
    void 페이징_TOP_FIRST() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        List<Member> result1 = memberRepository.findTop2By();

        for (Member member : result1) {
            System.out.println("member = " + member);
        }

        System.out.println("-----------------------------");

        List<Member> result2 = memberRepository.findFirst3By();

        for (Member member : result2) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @DisplayName("카운트 쿼리 분리")
    void 카운트_쿼리_분리() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 4;

        PageRequest pageRequest = PageRequest.of(offset, limit);
        Page<Member> result = memberRepository.findMemberAllCountByAge(age, pageRequest);
    }

    @Test
    @DisplayName("벌크성 수정 쿼리")
    void 벌크성_수정_쿼리() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 15));
        memberRepository.save(new Member("member3", 19));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 25));

        int resultCount = memberRepository.bulkAgePlus(20);
        assertThat(resultCount).isEqualTo(2);

        em.flush();
        em.clear();

        List<Member> member4 = memberRepository.findByUsername("member4");

        for (Member member : member4) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @DisplayName("Entity Graph")
    void entityGraph() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("userA", 18, teamA));
        memberRepository.save(new Member("userA", 39, teamB));

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getTeamName() = " + member.getTeam().getTeamName());
        }


    }

    @Test
    @DisplayName("JPA 쿼리 힌트")
    void queryHint() {
        memberRepository.save(new Member("userA", 10));
        em.flush();
        em.clear();

        Member userA = memberRepository.findReadOnlyByUsername("userA");
        userA.setUsername("userB");

        em.flush(); // Update Query 가 실행되지 않는다.
    }

    @Test
    @DisplayName("사용자 정의 리포지토리 구현")
    void 사용자_정의_리포지토리_구현() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 15));
        memberRepository.save(new Member("member3", 19));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 25));

        List<Member> result = memberRepository.findMemberCustom();

        for (Member member : result) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }
    }

    @Test
    @DisplayName("명세 기능 사용")
    void specification() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member user1 = new Member("user1", 10, teamA);
        Member user2 = new Member("user2", 20, teamA);
        em.persist(user1);
        em.persist(user2);

        em.flush();
        em.clear();

        Specification<Member> spec = MemberSpec.username("user1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
    }
}