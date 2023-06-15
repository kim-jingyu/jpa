package jpabasic.real.domain.item;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
public class Movie extends Item{
    private String director;
    private String actor;

    public Movie() {
    }
}
