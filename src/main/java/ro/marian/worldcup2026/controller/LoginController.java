package ro.marian.worldcup2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ro.marian.worldcup2026.model.AppUser;
import ro.marian.worldcup2026.repository.AppUserRepository;

import java.util.Optional;

@Controller
public class LoginController {

    private final AppUserRepository appUserRepository;

    public LoginController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        Optional<AppUser> optUser = appUserRepository.findByUsername(username);

        if (optUser.isEmpty() || !optUser.get().getPassword().equals(password)) {
            model.addAttribute("error", "Utilizator sau parola incorecta.");
            return "login";
        }

        AppUser user = optUser.get();

        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USERNAME", user.getUsername());
        session.setAttribute("FULL_NAME", user.getFullName());
        session.setAttribute("ROLE", user.getRole());

        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}