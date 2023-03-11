package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.Antwort;
import com.example.userverwaltung.domain.Frage;
import com.example.userverwaltung.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FrageRepository extends JpaRepository<Frage, Long> {
    @Query("""
            select f
            from Frage f
            where f not in (
                select a.frage
                from Antwort a
                where a.beantwortetVon = :userEntity
            ) and CURRENT_DATE < f.ablaufDatum
            """)
    List<Frage> getFragenForUser(UserEntity userEntity);

    @Query("""
            select count(a)
            from Antwort a
            where a.frage.id = :id and a.antwort = :vote
            """)
    Integer getNumberOfVotes(Long id, int vote);
}
