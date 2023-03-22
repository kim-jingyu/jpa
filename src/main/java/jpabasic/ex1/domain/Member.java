package jpabasic.ex1.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",      // 식별자 생성기 이름
        sequenceName = "MEMBER_SEQ",        // 데이터베이스에 등록되어 있는 시퀀스 이름
        initialValue = 1,
        allocationSize = 1
)
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
    @Column(name = "name")
    private String username;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    @Lob
    private String description;

    public Member() {
    }

    public Member(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
