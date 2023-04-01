package practice.jpashop.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.jpashop.domain.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    /**
     * 회원 저장
     */
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    /**
     * 아이디로 회원 찾기
     */
    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    /**
     * 회원 전부 찾기
     */
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    /**
     * 회원 이름으로 회원 찾기
     */
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.username = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
