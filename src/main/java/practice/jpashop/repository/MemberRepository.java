package practice.jpashop.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.jpashop.domain.Member;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }
}
