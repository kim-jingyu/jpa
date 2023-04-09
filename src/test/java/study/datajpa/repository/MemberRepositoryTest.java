package study.datajpa.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

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
}