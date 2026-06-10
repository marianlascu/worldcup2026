package ro.marian.worldcup2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ro.marian.worldcup2026.dto.KnockoutMatchView;
import ro.marian.worldcup2026.model.Tournament;
import ro.marian.worldcup2026.repository.MatchGameRepository;
import ro.marian.worldcup2026.repository.TournamentRepository;
import ro.marian.worldcup2026.service.KnockoutService;
import ro.marian.worldcup2026.service.MatchScoreService;
import ro.marian.worldcup2026.service.StandingService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class TournamentsController {

    private final TournamentRepository tournamentRepository;
    private final MatchGameRepository matchGameRepository;
    private final StandingService standingService;
    private final MatchScoreService matchScoreService;
    private final KnockoutService knockoutService;

    public TournamentsController(TournamentRepository tournamentRepository,
                                 MatchGameRepository matchGameRepository,
                                 StandingService standingService,
                                 MatchScoreService matchScoreService,
                                 KnockoutService knockoutService) {
        this.tournamentRepository = tournamentRepository;
        this.matchGameRepository = matchGameRepository;
        this.standingService = standingService;
        this.matchScoreService = matchScoreService;
        this.knockoutService = knockoutService;
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

        model.addAttribute("tournaments", tournamentRepository.findAllByOrderByNameAsc());

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
        String view = openTournamentTab(id, "tables", session, model);

        if (!"tournaments/tournament-layout".equals(view)) {
            return view;
        }

        model.addAttribute("groups", standingService.buildGroupStandings(id));

        return view;
    }

    @GetMapping("/tournaments/{id}/teams")
    public String tournamentTeams(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {
        String view = openTournamentTab(id, "teams", session, model);

        if (!"tournaments/tournament-layout".equals(view)) {
            return view;
        }

        model.addAttribute("groups", standingService.buildGroupStandings(id));

        return view;
    }

    @GetMapping("/tournaments/{id}/knockout")
    public String tournamentKnockout(@PathVariable Long id,
                                      HttpSession session,
                                      Model model) {
        String view = openTournamentTab(id, "knockout", session, model);

        if (!"tournaments/tournament-layout".equals(view)) {
            return view;
        }

        List<KnockoutMatchView> matches = knockoutService.buildKnockoutMatches(id);

        List<KnockoutMatchView> r32 = matches.stream()
                .filter(m -> stageIs(m.getStage(), "ROUND_OF_32"))
                .toList();

        List<KnockoutMatchView> r16 = matches.stream()
                .filter(m -> stageIs(m.getStage(), "ROUND_OF_16"))
                .toList();

        List<KnockoutMatchView> qf = matches.stream()
                .filter(m -> stageIs(m.getStage(), "QUARTER_FINAL"))
                .toList();

        List<KnockoutMatchView> sf = matches.stream()
                .filter(m -> stageIs(m.getStage(), "SEMI_FINAL"))
                .toList();

        KnockoutMatchView finalMatch = matches.stream()
                .filter(m -> stageIs(m.getStage(), "FINAL"))
                .findFirst()
                .orElse(null);

        KnockoutMatchView thirdPlace = matches.stream()
                .filter(m -> stageIs(m.getStage(), "THIRD_PLACE"))
                .findFirst()
                .orElse(null);

        List<KnockoutMatchView> r32Ordered = sortByMatchOrder(r32,
                74, 77, 73, 75, 76, 78, 79, 80,
                83, 84, 81, 82, 86, 88, 85, 87);

        List<KnockoutMatchView> r16Ordered = sortByMatchOrder(r16,
                89, 90, 91, 92, 93, 94, 95, 96);

        List<KnockoutMatchView> qfOrdered = sortByMatchOrder(qf,
                97, 98, 99, 100);

        List<KnockoutMatchView> sfOrdered = sortByMatchOrder(sf,
                101, 102);

        model.addAttribute("r32Left", firstItems(r32Ordered, 8));
        model.addAttribute("r32Right", remainingItems(r32Ordered, 8));

        model.addAttribute("r16Left", firstItems(r16Ordered, 4));
        model.addAttribute("r16Right", remainingItems(r16Ordered, 4));

        model.addAttribute("qfLeft", firstItems(qfOrdered, 2));
        model.addAttribute("qfRight", remainingItems(qfOrdered, 2));

        model.addAttribute("sfLeft", firstItems(sfOrdered, 1));
        model.addAttribute("sfRight", remainingItems(sfOrdered, 1));

        model.addAttribute("finalMatch", finalMatch);
        model.addAttribute("thirdPlace", thirdPlace);

        return view;
    }

    @PostMapping("/tournaments/{tournamentId}/matches/{matchId}/manual-score")
    public String saveManualScore(@PathVariable Long tournamentId,
                                  @PathVariable Long matchId,
                                  @RequestParam(required = false) Integer manualScoreA,
                                  @RequestParam(required = false) Integer manualScoreB,
                                  HttpSession session) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        String username = String.valueOf(session.getAttribute("FULL_NAME"));

        matchScoreService.saveManualScore(matchId, manualScoreA, manualScoreB, username);

        return "redirect:/tournaments/" + tournamentId + "/matches";
    }

    @PostMapping("/tournaments/{tournamentId}/matches/{matchId}/validate-manual")
    public String validateManualScore(@PathVariable Long tournamentId,
                                      @PathVariable Long matchId,
                                      HttpSession session) {
        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        String username = String.valueOf(session.getAttribute("FULL_NAME"));

        matchScoreService.validateManualScore(matchId, username);

        return "redirect:/tournaments/" + tournamentId + "/tables";
    }

    @PostMapping("/tournaments/{tournamentId}/matches/{matchId}/validate-api")
    public String validateApiScore(@PathVariable Long tournamentId,
                                   @PathVariable Long matchId,
                                   HttpSession session) {
        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        String username = String.valueOf(session.getAttribute("FULL_NAME"));

        matchScoreService.validateApiScore(matchId, username);

        return "redirect:/tournaments/" + tournamentId + "/tables";
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
        model.addAttribute("contentFragment", contentFragmentFor(activeTab));
        model.addAttribute("pageBgImage", tournament.getBgImage());

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

    private String contentFragmentFor(String activeTab) {
        return switch (activeTab) {
            case "overview" -> "tournaments/tournament-overview :: content";
            case "matches" -> "tournaments/tournament-matches :: content";
            case "tables" -> "tournaments/tournament-tables :: content";
            case "teams" -> "tournaments/tournament-teams :: content";
            case "knockout" -> "tournaments/tournament-knockout :: content";
            default -> "tournaments/tournament-overview :: content";
        };
    }

    private boolean stageIs(String stage, String expected) {
        return stage != null && stage.equalsIgnoreCase(expected);
    }

    private <T> List<T> firstItems(List<T> list, int count) {
        int toIndex = Math.min(count, list.size());
        return list.subList(0, toIndex);
    }

    private <T> List<T> remainingItems(List<T> list, int startIndex) {
        int fromIndex = Math.min(startIndex, list.size());
        return list.subList(fromIndex, list.size());
    }

    private List<KnockoutMatchView> sortByMatchOrder(List<KnockoutMatchView> list,
                                                     Integer... order) {
        Map<Integer, Integer> positions = new HashMap<>();

        for (int i = 0; i < order.length; i++) {
            positions.put(order[i], i);
        }

        return list.stream()
                .sorted(Comparator.comparingInt(
                        m -> positions.getOrDefault(m.getMatchNo(), 9999)
                ))
                .toList();
    }
}