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
     *
     * @param username
     * @return List<Member>
     */
    @Query(name = "Member.findByUsername")
    // @Query 를 생략하고 메서드 이름만으로 Named 쿼리를 호출할 수 있다.
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 직접 쿼리 기능 정의
     *
     * @param username
     * @param age
     * @return
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    /**
     * 단순 값 하나 조회
     * JPA 값 타입 ( @Embedded ) 도 이 방식으로 조회할 수 있다.
     *
     * @return
     */
    @Query("select m from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.repository.MemberDto(m.id, m.username, t.teamName) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * 컬렉션 파라미터 바인딩
     * @param names
     * @return List<Member>
     */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);
}
