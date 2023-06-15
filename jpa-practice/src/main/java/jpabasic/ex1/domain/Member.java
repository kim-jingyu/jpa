package jpabasic.ex1.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Embedded
    private Period workPeriod;

    @Embedded
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="city",
                    column=@Column(name = "COMPANY_CITY")),
            @AttributeOverride(name="street",
                    column=@Column(name = "COMPANY_STREET")),
            @AttributeOverride(name = "zipcode",
                    column=@Column(name = "COMPANY_ZIPCODE")
            )
    })
    private Address companyAddress;

    // 값 타입 컬렉션
    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
    @Column(name = "FOOD_NAME") // String 처럼 값이 내가 지정하지 않은 하나일 때는 컬럼 명을 지정할 수 있다.
    private Set<String> favoriteFoods = new HashSet<>();

//    @ElementCollection
//    @CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))
//    private List<Address> addressesHistory = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressesHistory = new ArrayList<>();

    public Member() {
    }

    public Member(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
