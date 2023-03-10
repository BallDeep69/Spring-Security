package com.example.userverwaltung.presentation;

import com.example.userverwaltung.domain.Frage;
import com.example.userverwaltung.domain.Typ;
import com.example.userverwaltung.persistence.AntwortRepository;
import com.example.userverwaltung.persistence.FrageRepository;
import com.example.userverwaltung.persistence.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
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
        var user = userRepository.getReferenceById(username);
        if (user.getTyp() == Typ.USER) {
            assert principal != null;
            return displayUserOverview(principal, model);
        } else {
            return displayAdminOverview(principal, model);
        }
    }

    private String displayUserOverview(Principal principal, Model model) {
        model.addAttribute("unanswered_questions", frageRepository.getFragenForUser(userRepository.getReferenceById(principal.getName())));
        return "overview_user";
    }

    private String displayAdminOverview(Principal principal, Model model) {
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

}
