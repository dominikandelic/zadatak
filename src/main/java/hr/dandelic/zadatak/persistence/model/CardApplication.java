package hr.dandelic.zadatak.persistence.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "card_applications", indexes = @Index(name = "oib_idx", columnList = "oib"))
public class CardApplication extends BaseEntity{


    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Long oib;

    @Enumerated(EnumType.STRING)
    private CardApplicationStatus status;

    @Getter
    public enum CardApplicationStatus {
        NEW, IN_PROGRESS, CANCELLED, COMPLETED
    }

    public String toStringForFile() {
        return String.format("%s, %s, %s, %s", firstName, lastName, oib, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CardApplication that = (CardApplication) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
