package com.example.userverwaltung.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"frage_id", "beantwortet_von_mail_adresse"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class Antwort {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @NotNull
 //   @JoinColumn(name="frage_id")
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

    //    @AssertTrue
//    private boolean isAnswerValid(){
//        return beantwortetAm.isBefore(frage.getAblaufDatum());
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Antwort antwort = (Antwort) o;
        return id != null && Objects.equals(id, antwort.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
