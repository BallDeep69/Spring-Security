package com.example.userverwaltung.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"frage", "beantwortetVon"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Antwort {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private Frage frage;

    @ManyToOne
    @NotNull
    private User beantwortetVon;

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

    public Antwort(Frage frage, User beantwortetVon, LocalDate beantwortetAm, Integer antwort) {
        this.frage = frage;
        this.beantwortetVon = beantwortetVon;
        this.beantwortetAm = beantwortetAm;
        this.antwort = antwort;
    }

    @AssertTrue
    private boolean isAnswerValid(){
        return beantwortetAm.isBefore(frage.getAblaufDatum());
    }
}
