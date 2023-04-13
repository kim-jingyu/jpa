package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{
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

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.teamName) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * 컬렉션 파라미터 바인딩
     *
     * @param names
     * @return List<Member>
     */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 페이징과 정렬 사용
    Page<Member> findPageByUsername(String name, Pageable pageable); // Page -> count 쿼리 사용

    Slice<Member> findSliceByAge(int age, Pageable pageable); // Slice -> count 쿼리 사용 안함

    List<Member> findListByAge(int age, Pageable pageable); // List -> count 쿼리 사용 안함

    List<Member> findSortByUsername(String name, Sort sort); // Sort -> 정렬 기능

    Page<Member> findPageByAge(int age, Pageable pageable);

    // count 쿼리 분리
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Page<Member> findMemberAllCountByAge(int age, Pageable pageable);

    // Top, First
    List<Member> findTop2By();

    List<Member> findFirst3By();

    // 벌크성 수정 쿼리
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // EntityGraph 사용하기

    /**
     * 공통 메서드 오버라이딩
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /**
     * JPQL + 엔티티 그래프
     */
    @EntityGraph(attributePaths = {"team"})
    @Query(value = "select m from Member m")
    List<Member> findMemberEntityGraph1();

    // 메서드 이름으로 쿼리 + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    @EntityGraph("Member.all")
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph2();

    // JPA 힌트
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @QueryHints(value = {@QueryHint(name = "org.hibernate.readOnly", value = "true")}, forCounting = true)
    Page<Member> findForCountingByUsername(String username, Pageable pageable);

    // Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
