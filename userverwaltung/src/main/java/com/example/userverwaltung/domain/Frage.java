package com.example.userverwaltung.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Frage {
    @Id
    @GeneratedValue
    private Long id;

    @Length(max = 20)
    @NotNull
    private String bezeichnung;

    @Length(max = 200)
    @NotNull
    private String fragetext;

    @NotNull
    private LocalDate ablaufDatum;

    @OneToMany
    @ToString.Exclude
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
