package practice.jpashop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpashop.domain.Member;
import practice.jpashop.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     * @param member
     * @return 가입된 회원 아이디
     */
    @Transactional
    public Long join(Member member) {

        validationDuplicateMember(member); // 중복 회원 검증

        memberRepository.save(member);
        return member.getId();
    }

    private void validationDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getUsername());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 전체 회원 조회
     * @return 전체 회원
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 아이디로 회원 찾기
     * @param memberId
     * @return 아이디로 찾은 회원
     */
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
