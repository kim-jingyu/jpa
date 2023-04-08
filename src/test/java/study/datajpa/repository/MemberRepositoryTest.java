package study.datajpa.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
@Slf4j
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

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
}