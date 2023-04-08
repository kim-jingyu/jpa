package study.datajpa.repository;

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
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository jpaRepository;

    @Test
    void testMember() {
        Member member = new Member("userA");

        Member savedMember = jpaRepository.save(member);

        Member foundMember = jpaRepository.findOne(savedMember.getId());

        assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
        assertThat(foundMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(foundMember).isEqualTo(savedMember);     // 영속성 컨텍스트의 동일성 ( identity ) 보장, 1차 캐시
    }

    @Test
    @DisplayName("기본 CRUD 테스트")
    void basicCRUD() {
        Member userA = new Member("userA");
        Member userB = new Member("userB");
        jpaRepository.save(userA);
        jpaRepository.save(userB);

        // 단건 조회 검증
        Member foundUserA = jpaRepository.findById(userA.getId()).get();
        Member foundUserB = jpaRepository.findById(userB.getId()).get();

        // 리스트 조회 검증
        List<Member> members = jpaRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        // 카운트 검증
        long count = jpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        jpaRepository.delete(userA);
        jpaRepository.delete(userB);

        long deletedCount = jpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

}