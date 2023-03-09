package com.example.userverwaltung.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Frage {
    @Id
    @GeneratedValue
    private Long id;

    @Max(20)
    @NotNull
    private String bezeichnung;

    @Max(200)
    @NotNull
    private String fragetext;

    @NotNull
    private LocalDate ablaufDatum;

    @OneToMany
    private List<Antwort> antworten;

    public Frage(String bezeichnung, String fragetext, LocalDate ablaufDatum) {
        this.bezeichnung = bezeichnung;
        this.fragetext = fragetext;
        this.ablaufDatum = ablaufDatum;
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
