package com.example.userverwaltung.persistence;

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

    @Query("SELECT a.frage, a.antwort, COUNT(a) AS vote_count FROM Antwort a GROUP BY a.frage, a.antwort")
    List<Object[]> getNumberOfVotes();

    @Query("select case when count(a) > 0 then true else false end from Antwort a where a.frage = :frage and a.beantwortetVon = :user")
    boolean isAlreadyAnswered(Frage frage, UserEntity user);
}
