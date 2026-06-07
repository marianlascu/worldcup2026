package ro.marian.worldcup2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ro.marian.worldcup2026.repository.MatchGameRepository;

@Controller
public class MatchGameController {

    private final MatchGameRepository matchGameRepository;

    public MatchGameController(MatchGameRepository matchGameRepository) {
        this.matchGameRepository = matchGameRepository;
    }

    @GetMapping("/")
    public String home(HttpSession session) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        return "redirect:/main";
    }

    @GetMapping("/matches")
    public String matches(HttpSession session, Model model) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        model.addAttribute("matches", matchGameRepository.findAllByOrderByKickoffAtAscMatchNoAsc());
        model.addAttribute("fullName", session.getAttribute("FULL_NAME"));
        model.addAttribute("role", session.getAttribute("ROLE"));

        return "matches";
    }
}