package com.example.userverwaltung.presentation;

import com.example.userverwaltung.domain.Antwort;
import com.example.userverwaltung.domain.Frage;
import com.example.userverwaltung.domain.Typ;
import com.example.userverwaltung.persistence.AntwortRepository;
import com.example.userverwaltung.persistence.FrageRepository;
import com.example.userverwaltung.persistence.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
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
        if (principal == null) throw new IllegalArgumentException("User nicht gueltig");
        var username = principal.getName();
        model.addAttribute("greeting", "Hello " + username);
        var user = userRepository.findById(username).orElseThrow();
        if (user.getTyp() == Typ.ADMIN) {
            return displayAdminOverview(model);
        } else {
            return displayUserOverview(username, model);
        }
    }

    private String displayUserOverview(String username, Model model) {
        model.addAttribute(
                "unanswered_questions",
                frageRepository.getFragenForUser(userRepository.findById(username).orElseThrow())
        );
        return "overview_user";
    }

    private String displayAdminOverview(Model model) {
        System.out.println("Alle Fragen: ");
        for (var antwort : antwortRepository.findAll()) {
            System.out.println(antwort);
        }
        model.addAttribute("all_questions", frageRepository.findAll());
        model.addAttribute("summary_map", getSummaryMap());
        return "overview_admin";
    }

    private Map<Long, Map<Integer, Integer>> getSummaryMap() {
        var summary = new HashMap<Long, Map<Integer, Integer>>();
        var fragen = frageRepository.findAll();
        for (var frage : fragen) {
            var mapForQuestion = new HashMap<Integer, Integer>();
            for (int i = 1; i <= 3; i++) {
                Integer numberOfVotes = frageRepository.getNumberOfVotes(frage.getId(), i);
                mapForQuestion.put(i, numberOfVotes == null ? 0 : numberOfVotes);
            }
            summary.put(frage.getId(), mapForQuestion);
        }
        return summary;
    }

    @GetMapping("/fragen/neue_frage")
    public String getNewQuestionPage(Principal principal, Model model) {
        validateUserForRole(principal, Typ.ADMIN);
        if (!model.containsAttribute("new_frage"))
            model.addAttribute("new_frage", new Frage());
        return "new_frage";
    }

    @PostMapping("/fragen/neue_frage")
    public String handleNewQuestion(Model model, Principal principal, @Valid @ModelAttribute("new_frage") Frage frage, BindingResult bindingResult) {
        validateUserForRole(principal, Typ.ADMIN);
        if (bindingResult.hasErrors())
            return getNewQuestionPage(principal, model);
        frageRepository.save(frage);
        return "redirect:/fragen";
    }

    @GetMapping("/fragen/{id}")
    public String getQuestionAnswerPage(@PathVariable Long id, Model model) {
        var selectedFrage = frageRepository.findById(id).orElseThrow();
        model.addAttribute("frage", selectedFrage);
        model.addAttribute("new_antwort", new Antwort());
        return "new_antwort";
    }

    @PostMapping("/fragen/{id}")
    public String handleAnsweredQuestion(@PathVariable Long id, Principal principal, @ModelAttribute("new_antwort") Antwort new_antwort, Model model) {
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

    private void validateUserForRole(Principal principal, Typ typ){
        var user = userRepository.findById(principal.getName());
        if(user.isEmpty() || user.get().getTyp() != typ)
            throw new HttpStatusCodeException(HttpStatus.NOT_FOUND) {
                @Override
                public HttpStatusCode getStatusCode() {
                    return super.getStatusCode();
                }
            };
    }

    @GetMapping("/error")
    public String redirectToErrorPage() {
        return "error";
    }

}
