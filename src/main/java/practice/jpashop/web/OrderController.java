package practice.jpashop.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import practice.jpashop.domain.Item;
import practice.jpashop.domain.Member;
import practice.jpashop.service.ItemService;
import practice.jpashop.service.MemberService;
import practice.jpashop.service.OrderService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "/order")
    public String orderForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public String order(@RequestParam Long memberId, @RequestParam Long itemId, @RequestParam int count) {
        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }
}
