package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.Antwort;
import com.example.userverwaltung.domain.Frage;
import com.example.userverwaltung.domain.Typ;
import com.example.userverwaltung.domain.UserEntity;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public record DatabaseInit (FrageRepository frageRepository, UserRepository userRepository, AntwortRepository antwortenRepository) implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        var bcryptencoder = new BCryptPasswordEncoder();
        var users = List.of(
                new UserEntity("p.schoeppl@htlstp.at", Typ.ADMIN, bcryptencoder.encode("kotbert34"), "admin"),
                new UserEntity("s.piringer@htlstp.at", Typ.USER, bcryptencoder.encode("simsi123"), "user"),
                new UserEntity("e.pils@htlstp.at", Typ.USER, bcryptencoder.encode("Bernhard"), "user"),
                new UserEntity("m.hrazdira@htlstp.at", Typ.USER, bcryptencoder.encode("freddy_keine_show"), "user")
        );
        users = userRepository.saveAll(users);
        var fragen = List.of(
                new Frage("freddys show","Macht Freddy heute Show?", LocalDate.of(2023,2,1)),
                new Frage("SQL Verbrecher","Verdient der SQL Verbrecher lebenslange Haft?", LocalDate.of(2023,4,9)),
                new Frage("Geogebra Bandit", """
                        Wie viele Schritte braucht der Geogebra Bandit vom Lehrerzimmer bis zur 5BHIF?
                        "trifft völlig zu" für 1 Schritt, 
                        "trifft teilweise zu" für 2 Schritte,
                        "trifft nicht zu" für 3 Schritte
                        """, LocalDate.of(2023,4,9)),
                new Frage("frankthetank","Der Elektrowizard hat mehr Drip als die ganze Abteilung zusammen", LocalDate.of(2023,4,9)),
                new Frage("frankthetank2","Neuer Sommerskin für den Elektrowizard??", LocalDate.of(2023,4,9)),
                new Frage("FrankElectroWizard", "ELLECCCTRIIIIFYYYYYY", LocalDate.of(2023,4,9))
        );
        fragen = frageRepository.saveAll(fragen);
        var antworten = List.of(
                new Antwort(fragen.get(0), users.get(0), LocalDate.of(2023,1,1), 3),
                new Antwort(fragen.get(0), users.get(1), LocalDate.of(2023,1,1), 2),
                new Antwort(fragen.get(1), users.get(0), LocalDate.of(2023,3,6), 1)
        );
        antwortenRepository.saveAll(antworten);
    }
}
