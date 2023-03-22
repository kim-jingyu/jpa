package jpabasic.ex2.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Team {

    @Id @GeneratedValue
    private Long id;
    private String name;

    public Team() {
    }
}
