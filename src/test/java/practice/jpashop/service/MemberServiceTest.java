package practice.jpashop.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import practice.jpashop.domain.Member;
import practice.jpashop.repository.MemberRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입() {
        //Given
        Member member = new Member();
        member.setUsername("user");

        //When
        Long savedId = memberService.join(member);

        //Then
        Member foundMember = memberRepository.findById(savedId);
        Assertions.assertThat(foundMember).isEqualTo(member);
    }

    @Test
    @DisplayName("중복 회원가입 방지 테스트")
    void 중복_회원_예외(){
        // Given
        Member member1 = new Member();
        member1.setUsername("user");

        Member member2 = new Member();
        member2.setUsername("user");

        // When
        memberService.join(member1);

        // Then
        Assertions.assertThatThrownBy(() -> memberService.join(member2))
                .isInstanceOf(IllegalStateException.class);
    }
}