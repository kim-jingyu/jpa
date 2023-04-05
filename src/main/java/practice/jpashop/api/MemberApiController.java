package practice.jpashop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practice.jpashop.domain.Member;
import practice.jpashop.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse joinV1(@RequestBody @Validated Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse joinV2(@RequestBody @Validated CreateMemberRequest request) {
        Member member = new Member();
        member.setUsername(request.getUsername());
        return new CreateMemberResponse(memberService.join(member));
    }

    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateV2(@PathVariable Long id, @RequestBody @Validated UpdateMemberRequest request) {
        memberService.update(id, request.getUsername());
        Member member = memberService.findById(id);
        return new UpdateMemberResponse(member.getId(), member.getUsername());
    }

    @Data
    static class CreateMemberRequest {
        private String username;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest{
        private String username;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String username;
    }
}
