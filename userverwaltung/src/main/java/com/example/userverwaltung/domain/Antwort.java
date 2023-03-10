package com.example.userverwaltung.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"frage_id", "beantwortet_von_mail_adresse"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class Antwort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name="frage_id")
    private Frage frage;

    @ManyToOne
    @NotNull
    private UserEntity beantwortetVon;

    @PastOrPresent
    @NotNull
    private LocalDate beantwortetAm;

    /**
     * trifft völlig zu: 1
     * trifft teilweise zu: 2
     * trifft nicht zu: 3
     */
    @Min(1)
    @Max(3)
    @NotNull
    private Integer antwort;

    public Antwort(Frage frage, UserEntity beantwortetVon, LocalDate beantwortetAm, Integer antwort) {
        this.frage = frage;
        this.beantwortetVon = beantwortetVon;
        this.beantwortetAm = beantwortetAm;
        this.antwort = antwort;
    }

    @AssertTrue
    private boolean isAnswerValid(){
        return beantwortetAm.isBefore(frage.getAblaufDatum());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Antwort antwort1 = (Antwort) o;
        return Objects.equals(id, antwort1.id) && Objects.equals(frage, antwort1.frage) && Objects.equals(beantwortetVon, antwort1.beantwortetVon) && Objects.equals(beantwortetAm, antwort1.beantwortetAm) && Objects.equals(antwort, antwort1.antwort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frage, beantwortetVon, beantwortetAm, antwort);
    }
}
