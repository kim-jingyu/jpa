package study.datajpa.dto;

import org.springframework.beans.factory.annotation.Value;

/**
 * Projections
 * 엔티티 대신에 DTO 를 편리하게 조회할 때 사용
 * 전체 엔티티가 아니라 회원 이름만 조회하고 싶을 때
 */
public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
