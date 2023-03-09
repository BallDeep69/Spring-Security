package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.Antwort;
import com.example.userverwaltung.domain.Frage;
import com.example.userverwaltung.domain.Typ;
import com.example.userverwaltung.domain.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public record DatabaseInit (FrageRepository frageRepository, UserRepository userRepository, AntwortenRepository antwortenRepository) implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        var bcryptencoder = new BCryptPasswordEncoder();
        var users = List.of(
                new User("p.schoeppl@htlstp.at", Typ.ADMIN, bcryptencoder.encode("lululasdlfj345"), "user"),
                new User("s.piringer@htlstp.at", Typ.USER, bcryptencoder.encode("simsi123"), "user"),
                new User("e.pils@htlstp.at", Typ.USER, bcryptencoder.encode("Bernhard"), "user"),
                new User("m.hrazdira@htlstp.at", Typ.USER, bcryptencoder.encode("freddy_keine_show"), "user")
        );
        userRepository.saveAll(users);
        var fragen = List.of(
                new Frage("freddys show","Macht Freddy heute Show?", LocalDate.of(2023,2,1)),
                new Frage("SQL Verbrecher","Verdient der SQL Verbrecher lebenslange Haft?", LocalDate.of(2023,4,9)),
                new Frage("Geogebra Bandit","\"Hippiti Hoppiti your HTL Abschluss is now my property\"", LocalDate.of(2023,4,9)),
                new Frage("frankthetank","\"Der Elektrowizard hat mehr Drip als die ganze Abteilung zusammen\"", LocalDate.of(2023,4,9)),
                new Frage("frankthetank2","\"Da Reichel Fronz hot ins Pissoa eine gschissn\"", LocalDate.of(2023,4,9))
        );
        frageRepository.saveAll(fragen);
        var antworten = List.of(
                new Antwort(fragen.get(0), users.get(0), LocalDate.of(2023,1,1), 3),
                new Antwort(fragen.get(0), users.get(1), LocalDate.of(2023,1,1), 2),
                new Antwort(fragen.get(1), users.get(0), LocalDate.of(2023,3,6), 1)
        );
        antwortenRepository.saveAll(antworten);
    }
}
