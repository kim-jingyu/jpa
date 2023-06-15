package jpabasic.jpql.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Cellphone extends Product{
    private String brandName;
    private String model;

    public Cellphone() {
    }

    @Override
    public String toString() {
        return "Cellphone{" +
                "brandName='" + brandName + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
