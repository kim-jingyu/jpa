package jpabasic.ex2.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member  extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // PK
//    private Long teamId;        // FK, 참조 대신에 외래 키를 그대로 사용한다.
    private String userName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    public Member() {
    }
}
