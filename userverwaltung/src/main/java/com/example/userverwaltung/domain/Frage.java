package com.example.userverwaltung.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import lombok.*;
import org.aspectj.lang.annotation.DeclareError;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Frage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Length(max = 20, message = "zu lang")
    @NotNull(message = "muss ausgewählt sein")
    private String bezeichnung;

    @Length(max = 200, message = "zu lang")
    @NotNull(message = "muss ausgewählt sein")
    private String fragetext;

    @NotNull(message = "muss ausgewählt sein")
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate ablaufDatum;


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
