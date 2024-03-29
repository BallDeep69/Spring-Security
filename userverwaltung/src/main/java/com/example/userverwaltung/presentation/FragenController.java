package com.example.userverwaltung.presentation;

import com.example.userverwaltung.domain.Antwort;
import com.example.userverwaltung.domain.Frage;
import com.example.userverwaltung.domain.Typ;
import com.example.userverwaltung.domain.exception.UserNotValidException;
import com.example.userverwaltung.persistence.AntwortRepository;
import com.example.userverwaltung.persistence.AntwortService;
import com.example.userverwaltung.persistence.FrageRepository;
import com.example.userverwaltung.persistence.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping
public record FragenController(FrageRepository frageRepository, AntwortRepository antwortRepository,
                               UserRepository userRepository, AntwortService antwortService) {

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
        model.addAttribute("vote_summary", antwortService.getVoteSummary().entrySet());
        return "overview_admin";
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
    public String getQuestionAnswerPage(@PathVariable Long id, Principal principal, Model model) {
        var selectedFrage = frageRepository.findById(id).orElseThrow();
        var user = userRepository.findById(principal.getName()).orElseThrow();
        if (frageRepository.isAlreadyAnswered(selectedFrage, user)) throw new UserNotValidException();
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
            return getQuestionAnswerPage(id, principal, model);
        new_antwort.setFrage(selectedFrage);
        new_antwort.setBeantwortetVon(user);
        new_antwort.setBeantwortetAm(LocalDate.now());
        var saved = antwortRepository.save(new_antwort);
        System.out.println(saved);
        return "redirect:/fragen";
    }

    private void validateUserForRole(Principal principal, Typ typ) {
        var user = userRepository.findById(principal.getName());
        if (user.isEmpty() || user.get().getTyp() != typ)
            throw new UserNotValidException();
    }

    @GetMapping("/error")
    public String redirectToErrorPage() {
        return "error";
    }

}
