package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.dto.MemberDto;
import study.datajpa.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    // 도메인 클래스 컨버터 사용 전
    @GetMapping("/members/before/{id}")
    public String findMember1(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터 사용 후
    @GetMapping("/members/after/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members/list")
    public Slice<Member> findList(Pageable pageable) {
        return memberRepository.findSliceByAge(10, pageable);
    }

    @GetMapping("/members/list2")
    public Slice<Member> findList2(Pageable pageable) {
        return memberRepository.findSliceByAge(10, pageable);
    }

    // 페이징과 정렬
    @GetMapping("/members")
    public Page<Member> list(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    // 페이징과 정렬 - 개별 설정 (@PageableDefault)
    @GetMapping("/members_page")
    public Page<Member> list2(@PageableDefault(size = 5, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    // 페이징과 정렬 - 접두사
    @GetMapping("/members_prefix")
    public Page<Member> list3(@Qualifier("member") Pageable memberPageable,
                              @Qualifier("order") Pageable orderPageable) {
        return memberRepository.findAll(memberPageable);
    }

    // 페이징과 정렬 - Page 내용을 DTO 로 변환
    @GetMapping("/members_dto")
    public Page<MemberDto> list4(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

    // Page 를 1부터 시작하기, 원하는 API 규약을 만들어서 임의의 객체로 반환
    @GetMapping("/members_page_request")
    public MyPage<MemberDto> page() {
        PageRequest request = PageRequest.of(1, 2);

        return (MyPage<MemberDto>) memberRepository.findAll(request)
                .map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
