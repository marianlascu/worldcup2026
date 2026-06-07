package ro.marian.worldcup2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ro.marian.worldcup2026.model.Tournament;
import ro.marian.worldcup2026.repository.MatchGameRepository;
import ro.marian.worldcup2026.repository.TournamentRepository;

import java.util.Optional;

@Controller
public class TournamentsController {

    private final TournamentRepository tournamentRepository;
    private final MatchGameRepository matchGameRepository;

    public TournamentsController(TournamentRepository tournamentRepository,
                                 MatchGameRepository matchGameRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchGameRepository = matchGameRepository;
    }

    @GetMapping("/tournaments")
    public String tournaments(HttpSession session, Model model) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        addUserInfo(session, model);

        model.addAttribute("tournaments", tournamentRepository.findAllByOrderByNameAsc());
        model.addAttribute("selectedTournamentId", session.getAttribute("TOURNAMENT_ID"));
        model.addAttribute("selectedTournamentName", session.getAttribute("TOURNAMENT_NAME"));

        return "tournaments";
    }

    @PostMapping("/tournaments/select")
    public String selectTournament(@RequestParam Long tournamentId,
                                   HttpSession session) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        Optional<Tournament> optTournament = tournamentRepository.findById(tournamentId);

        if (optTournament.isEmpty()) {
            return "redirect:/tournaments";
        }

        Tournament tournament = optTournament.get();
        saveTournamentInSession(session, tournament);

        return "redirect:/tournaments/" + tournament.getId() + "/overview";
    }

    @PostMapping("/tournaments/clear")
    public String clearTournament(HttpSession session) {

        session.removeAttribute("TOURNAMENT_ID");
        session.removeAttribute("TOURNAMENT_CODE");
        session.removeAttribute("TOURNAMENT_NAME");

        return "redirect:/tournaments";
    }

    @GetMapping("/tournaments/{id}/overview")
    public String tournamentOverview(@PathVariable Long id,
                                     HttpSession session,
                                     Model model) {

        String view = openTournamentTab(id, "overview", session, model);

        if (!"tournaments/tournament-layout".equals(view)) {
            return view;
        }

        model.addAttribute(
                "tournaments",
                tournamentRepository.findAllByOrderByNameAsc()
        );

        return view;
    }

    @GetMapping("/tournaments/{id}/matches")
    public String tournamentMatches(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {

        String view = openTournamentTab(id, "matches", session, model);

        if (!"tournaments/tournament-layout".equals(view)) {
            return view;
        }

        model.addAttribute("matches",
                matchGameRepository.findByTournamentIdOrderByKickoffAtAscMatchNoAsc(id));

        return view;
    }

    @GetMapping("/tournaments/{id}/tables")
    public String tournamentTables(@PathVariable Long id,
                                   HttpSession session,
                                   Model model) {

        return openTournamentTab(id, "tables", session, model);
    }

    @GetMapping("/tournaments/{id}/teams")
    public String tournamentTeams(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {

        return openTournamentTab(id, "teams", session, model);
    }

    private String openTournamentTab(Long id,
                                     String activeTab,
                                     HttpSession session,
                                     Model model) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        Optional<Tournament> optTournament = tournamentRepository.findById(id);

        if (optTournament.isEmpty()) {
            return "redirect:/tournaments";
        }

        Tournament tournament = optTournament.get();

        saveTournamentInSession(session, tournament);
        addUserInfo(session, model);

        model.addAttribute("selectedTournament", tournament);
        model.addAttribute("selectedTournamentId", tournament.getId());
        model.addAttribute("selectedTournamentName", tournament.getName());
        model.addAttribute("activeTab", activeTab);

        return "tournaments/tournament-layout";
    }

    private void saveTournamentInSession(HttpSession session, Tournament tournament) {
        session.setAttribute("TOURNAMENT_ID", tournament.getId());
        session.setAttribute("TOURNAMENT_CODE", tournament.getCode());
        session.setAttribute("TOURNAMENT_NAME", tournament.getName());
    }

    private void addUserInfo(HttpSession session, Model model) {
        model.addAttribute("fullName", session.getAttribute("FULL_NAME"));
        model.addAttribute("role", session.getAttribute("ROLE"));
    }
}