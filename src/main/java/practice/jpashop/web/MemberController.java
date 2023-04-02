package practice.jpashop.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import practice.jpashop.domain.Address;
import practice.jpashop.domain.Member;
import practice.jpashop.service.MemberService;


@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입 폼
    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    // 회원가입 프로세스
    @PostMapping(value = "/members/new")
    public String create(@Validated MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setUsername(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }
}
