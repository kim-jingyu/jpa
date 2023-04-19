package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

/**
 * 동적 쿼리와 성능 최적화 조회
 * 조회 최적화용 DTO
 */
@Data
public class MemberTeamDto {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
