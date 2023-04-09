package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloByUsernameContaining(String username);

    /**
     * 스프링 데이터 JPA로 NamedQuery 사용
     * @param username
     * @return List<Member>
     */
    @Query(name = "Member.findByUsername")  // @Query 를 생략하고 메서드 이름만으로 Named 쿼리를 호출할 수 있다.
    List<Member> findByUsername(@Param("username") String username);
}
