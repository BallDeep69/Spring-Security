package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.Frage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AntwortService {

    @Autowired
    private FrageRepository frageRepo;

    public Map<Frage, Map<Integer, Long>> getVoteSummary(){
        var map = new HashMap<Frage, Map<Integer, Long>>();
        var rows = frageRepo.getNumberOfVotes();
        frageRepo.findAll().forEach(frage -> map.put(frage, new HashMap<>()));
        for(var row : rows){
            Frage frage = (Frage) row[0];
            Integer antwort = (Integer) row[1];
            Long anzahl = (Long) row[2];
            map.get(frage).put(antwort, anzahl);
        }
        return map;
    }

}
