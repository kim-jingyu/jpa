package jpabasic.ex2.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;            // PK
    private Long teamId;        // FK, 참조 대신에 외래 키를 그대로 사용한다.
    private String username;

    public Member() {
    }
}
