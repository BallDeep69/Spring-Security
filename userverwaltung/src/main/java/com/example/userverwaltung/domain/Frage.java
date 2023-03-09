package com.example.userverwaltung.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Frage {
    @Id
    @GeneratedValue
    private Long id;

    @Max(20)
    private String bezeichnung;

    @Max(200)
    private String fragetext;

    @Min(1)
    @Max(3)
    @Setter(AccessLevel.NONE)
    private int antwort;

    @NonNull
    private LocalDate ablaufDatum;

    public void setAntwort(int antwort) {
        if (this.antwort == 0 && ablaufDatum.isAfter(LocalDate.now())) {
            this.antwort = antwort;
        } else {
            throw new IllegalArgumentException("Frage kann nur einmal innerhalb der gueltigen Zeit beantwortet werden");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Frage frage = (Frage) o;
        return id != null && Objects.equals(id, frage.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
