package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.Antwort;
import com.example.userverwaltung.domain.Frage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FrageRepository extends JpaRepository<Frage, Long> {
    @Query("select f.antworten from Frage f where f.id = :id")
    List<Antwort> getAntwortenFromFrage(Long id);
}
