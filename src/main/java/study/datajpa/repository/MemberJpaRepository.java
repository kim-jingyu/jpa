package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {
    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    /**
     * 삭제
     * @param member
     */
    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m" +
                        " where m.username = :username" +
                        " and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    /**
     * JPA를 직접 사용해서 Named 쿼리 호출
     * @param username
     * @return List<Member>
     */
    public List<Member> findByUsername(String username) {
        return em.createQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    /**
     * 순수 JPA 페이징 코드
     */
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery(
                "select m from Member m" +
                " where m.age = :age" +
                " order by m.username desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery(
                "select count(m) from Member m" +
                        " where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }
}
