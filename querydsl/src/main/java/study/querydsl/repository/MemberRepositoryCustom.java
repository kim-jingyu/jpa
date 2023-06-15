package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import java.util.List;

/**
 * 사용자 정의 인터페이스
 */
public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);

    // 사용자 정의 인터페이스에 페이징 2가지 추가

    // 전체 카운트를 한번에 조회하는 단순한 방법
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
    // 데이터 내용과 전체 카운트를 별도로 조회하는 방법
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
