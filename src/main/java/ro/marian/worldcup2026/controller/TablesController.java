package ro.marian.worldcup2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TablesController {

    @GetMapping("/tables")
    public String tables(HttpSession session, Model model) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        model.addAttribute("fullName", session.getAttribute("FULL_NAME"));
        model.addAttribute("role", session.getAttribute("ROLE"));

        return "tables";
    }
}