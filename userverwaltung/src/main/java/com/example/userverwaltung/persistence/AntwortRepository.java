package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.Antwort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AntwortRepository extends JpaRepository<Antwort, Long> {
}
