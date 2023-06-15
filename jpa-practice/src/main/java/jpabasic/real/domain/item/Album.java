package jpabasic.real.domain.item;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
public class Album extends Item {
    private String artist;
    private String etc;

    public Album() {
    }
}