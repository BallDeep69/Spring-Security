package com.example.userverwaltung.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {
    @Id
    @Email
    private String mailAdresse;

    @NotNull
    private Typ typ;

    @NotNull
    private String passwort;

    @NotNull
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return mailAdresse != null && Objects.equals(mailAdresse, user.mailAdresse);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
