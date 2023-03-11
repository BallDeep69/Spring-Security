package com.example.userverwaltung.presentation;

import com.example.userverwaltung.domain.Antwort;
import com.example.userverwaltung.domain.Frage;
import com.example.userverwaltung.domain.Typ;
import com.example.userverwaltung.persistence.AntwortRepository;
import com.example.userverwaltung.persistence.FrageRepository;
import com.example.userverwaltung.persistence.UserRepository;
import jakarta.validation.Valid;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public record FragenController(FrageRepository frageRepository, AntwortRepository antwortRepository,
                               UserRepository userRepository) {

    @GetMapping("login")
    public String displayLogin() {
        return "login";
    }

    @GetMapping("greeting")
    public String getGreeting(Principal principal, Model model) {
        var username = principal == null ? "User" : principal.getName();
        model.addAttribute("greeting", "Hello " + username);
        return "greeting";
    }

    @GetMapping("/fragen")
    public String displayOverview(Principal principal, Model model) {
        var username = principal == null ? "User" : principal.getName();
        model.addAttribute("greeting", "Hello " + username);
        var user = userRepository.findById(username).orElseThrow();
        if (user.getTyp() == Typ.USER) {
            return displayUserOverview(principal, model);
        } else {
            return displayAdminOverview(principal, model);
        }
    }

    private String displayUserOverview(Principal principal, Model model) {
        model.addAttribute(
                "unanswered_questions",
                frageRepository.getFragenForUser(userRepository.findById(principal.getName()).orElseThrow())
        );
        return "overview_user";
    }

    private String displayAdminOverview(Principal principal, Model model) {
        System.out.println("Alle Fragen: ");
        for(var antwort : antwortRepository.findAll()){
            System.out.println(antwort);
        }
        model.addAttribute("all_questions", frageRepository.findAll());
        model.addAttribute("summary_map", getSummaryMap());
        return "overview_admin";
    }

    private Map<Long, List<Integer>> getSummaryMap(){
        var summary = new HashMap<Long, List<Integer>>();
        var fragen = frageRepository.findAll();
        for(var frage : fragen){
            var listOfAnswers = new ArrayList<Integer>();
            for(int i = 1; i <= 3; i++){
                Integer number = frageRepository.getNumberOfVotes(frage.getId(), i);
                listOfAnswers.add(number == null ? 0 : number);
            }
            summary.put(frage.getId(), listOfAnswers);
        }
        return summary;
    }

    @GetMapping("/fragen/new_frage")
    public String getNewQuestionPage(Model model){
        if(!model.containsAttribute("new_frage"))
            model.addAttribute("new_frage", new Frage());
        return "new_frage";
    }

    @PostMapping("/fragen/new_frage")
    public String handleNewQuestion(Model model, @Valid @ModelAttribute("new_frage") Frage frage, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return getNewQuestionPage(model);
        frageRepository.save(frage);
        return "redirect:/fragen";    //kein .html file, sondern eine route
    }

    @GetMapping("/fragen/{id}")
    public String getQuestionAnswerPage(@PathVariable Long id, Model model){
        var selectedFrage = frageRepository.findById(id).orElseThrow();
        model.addAttribute("frage", selectedFrage);
        model.addAttribute("new_antwort", new Antwort());
        return "new_antwort";
    }

    @PostMapping("/fragen/{id}")
    public String handleAnsweredQuestion(@PathVariable Long id, Principal principal, @ModelAttribute("new_antwort") Antwort new_antwort, Model model){
        new_antwort.setId(null);
        var selectedFrage = frageRepository.findById(id).orElseThrow();
        var user = userRepository.findById(principal.getName()).orElseThrow();
        if (new_antwort.getAntwort() == null)
            return getQuestionAnswerPage(id, model);
        new_antwort.setFrage(selectedFrage);
        new_antwort.setBeantwortetVon(user);
        new_antwort.setBeantwortetAm(LocalDate.now());
        var saved = antwortRepository.save(new_antwort);
        System.out.println(saved);
        return "redirect:/fragen";
    }

}
