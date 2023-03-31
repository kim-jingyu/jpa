package jpabasic.jpql.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Car extends Product {
    private String brandName;
    private String model;

    public Car() {
    }
}
