package com.example.userverwaltung.presentation;

import com.example.userverwaltung.persistence.AntwortRepository;
import com.example.userverwaltung.persistence.FrageRepository;
import com.example.userverwaltung.persistence.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping
public record FragenController(FrageRepository frageRepository, AntwortRepository antwortRepository, UserRepository userRepository) {

    @GetMapping
    public String displayLogin(){
        return "login";
    }

    @GetMapping("greeting")
    public String getGreeting(Principal principal, Model model) {
        var username = principal == null ? "User" : principal.getName();
        model.addAttribute("greeting", "Hello " + username);
        return "greeting";
    }

    @GetMapping("/fragen")
    public String displayFragen(Principal principal, Model model){
        model.addAttribute("unanswered_questions", frageRepository.getFragenForUser(userRepository.getReferenceById(principal.getName())));
        return "overview_user";
    }

}
