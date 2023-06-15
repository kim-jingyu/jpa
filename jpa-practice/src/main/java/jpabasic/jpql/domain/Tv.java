package jpabasic.jpql.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Tv extends Product {
    private String brandName;
    private String size;

    public Tv() {
    }
}
