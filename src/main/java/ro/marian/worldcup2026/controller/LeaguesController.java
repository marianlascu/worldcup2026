package ro.marian.worldcup2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ro.marian.worldcup2026.dto.LeagueListRow;
import ro.marian.worldcup2026.dto.LeagueMemberRow;
import ro.marian.worldcup2026.model.AppUser;
import ro.marian.worldcup2026.model.League;
import ro.marian.worldcup2026.model.LeagueMember;
import ro.marian.worldcup2026.model.Tournament;
import ro.marian.worldcup2026.repository.AppUserRepository;
import ro.marian.worldcup2026.repository.LeagueMemberRepository;
import ro.marian.worldcup2026.repository.LeagueRepository;
import ro.marian.worldcup2026.repository.TournamentRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class LeaguesController {

    private final LeagueRepository leagueRepository;
    private final LeagueMemberRepository leagueMemberRepository;
    private final TournamentRepository tournamentRepository;
    private final AppUserRepository appUserRepository;

    public LeaguesController(LeagueRepository leagueRepository,
                             LeagueMemberRepository leagueMemberRepository,
                             TournamentRepository tournamentRepository,
                             AppUserRepository appUserRepository) {
        this.leagueRepository = leagueRepository;
        this.leagueMemberRepository = leagueMemberRepository;
        this.tournamentRepository = tournamentRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/leagues")
    public String leagues(HttpSession session, Model model) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        Long userId = currentUserId(session);

        List<LeagueMember> memberships =
                leagueMemberRepository.findByUserIdAndActiveYnOrderByJoinedAtDesc(userId, "Y");

        Map<Long, League> leaguesById = leagueRepository.findAllById(
                memberships.stream()
                        .map(LeagueMember::getLeagueId)
                        .toList()
        ).stream().collect(Collectors.toMap(League::getId, l -> l));

        Map<Long, Tournament> tournamentsById = tournamentRepository.findAllById(
                leaguesById.values()
                        .stream()
                        .map(League::getTournamentId)
                        .toList()
        ).stream().collect(Collectors.toMap(Tournament::getId, t -> t));

        List<LeagueListRow> myLeagues = memberships.stream()
                .map(m -> {
                    League league = leaguesById.get(m.getLeagueId());
                    Tournament tournament = league == null ? null : tournamentsById.get(league.getTournamentId());

                    return new LeagueListRow(
                            m.getLeagueId(),
                            league == null ? "(missing league)" : league.getName(),
                            league == null ? "" : league.getCode(),
                            tournament == null ? "" : tournament.getName(),
                            m.getRole(),
                            m.getJoinedAt()
                    );
                })
                .toList();

        addUserInfo(session, model);
        model.addAttribute("tournaments", tournamentRepository.findAllByOrderByNameAsc());
        model.addAttribute("myLeagues", myLeagues);

        return "leagues";
    }

    @PostMapping("/leagues/create")
    public String createLeague(@RequestParam String name,
                               @RequestParam Long tournamentId,
                               HttpSession session) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        Long userId = currentUserId(session);

        League league = new League();
        league.setName(name);
        league.setTournamentId(tournamentId);
        league.setOwnerUserId(userId);
        league.setCode(generateLeagueCode());

        League savedLeague = leagueRepository.save(league);

        LeagueMember ownerMember = new LeagueMember();
        ownerMember.setLeagueId(savedLeague.getId());
        ownerMember.setUserId(userId);
        ownerMember.setRole("OWNER");

        leagueMemberRepository.save(ownerMember);

        return "redirect:/leagues/" + savedLeague.getId();
    }

    @GetMapping("/leagues/{id}")
    public String leagueDetails(@PathVariable Long id,
                                HttpSession session,
                                Model model) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        Long userId = currentUserId(session);

        League league = leagueRepository.findById(id).orElse(null);

        if (league == null || !"Y".equalsIgnoreCase(league.getActiveYn())) {
            return "redirect:/leagues";
        }

        boolean isMember = leagueMemberRepository.findByLeagueIdAndUserId(id, userId)
                .filter(m -> "Y".equalsIgnoreCase(m.getActiveYn()))
                .isPresent();

        if (!isMember && !isAdmin(session)) {
            return "redirect:/leagues";
        }

        List<LeagueMember> members =
                leagueMemberRepository.findByLeagueIdAndActiveYnOrderByJoinedAtAsc(id, "Y");

        Map<Long, AppUser> usersById = appUserRepository.findAllById(
                members.stream()
                        .map(LeagueMember::getUserId)
                        .toList()
        ).stream().collect(Collectors.toMap(AppUser::getId, u -> u));

        List<LeagueMemberRow> memberRows = members.stream()
                .map(m -> {
                    AppUser user = usersById.get(m.getUserId());

                    return new LeagueMemberRow(
                            m.getUserId(),
                            user == null ? "" : user.getUsername(),
                            user == null ? "(missing user)" : user.getFullName(),
                            m.getRole(),
                            m.getJoinedAt()
                    );
                })
                .toList();

        boolean canEditLeague = canEditLeague(session, league);

        addUserInfo(session, model);
        model.addAttribute("league", league);
        model.addAttribute("members", memberRows);
        model.addAttribute("canEditLeague", canEditLeague);

        return "league-details";
    }

    @PostMapping("/leagues/{id}/rename")
    public String renameLeague(@PathVariable Long id,
                               @RequestParam String name,
                               HttpSession session) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        League league = leagueRepository.findById(id).orElse(null);

        if (league == null || !"Y".equalsIgnoreCase(league.getActiveYn())) {
            return "redirect:/leagues";
        }

        if (!canEditLeague(session, league)) {
            return "redirect:/leagues/" + id;
        }

        String cleanName = name == null ? "" : name.trim();

        if (!cleanName.isBlank()) {
            league.setName(cleanName);
            leagueRepository.save(league);
        }

        return "redirect:/leagues/" + id;
    }

    @PostMapping("/leagues/{id}/members/add")
    public String addMember(@PathVariable Long id,
                            @RequestParam String username,
                            HttpSession session) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        League league = leagueRepository.findById(id).orElse(null);

        if (league == null || !"Y".equalsIgnoreCase(league.getActiveYn())) {
            return "redirect:/leagues";
        }

        if (!canEditLeague(session, league)) {
            return "redirect:/leagues/" + id;
        }

        String cleanUsername = username == null ? "" : username.trim();

        if (cleanUsername.isBlank()) {
            return "redirect:/leagues/" + id;
        }

        AppUser user = appUserRepository
                .findByUsernameIgnoreCase(cleanUsername)
                .orElse(null);

        if (user == null) {
            return "redirect:/leagues/" + id;
        }

        boolean alreadyExists = leagueMemberRepository
                .findByLeagueIdAndUserId(id, user.getId())
                .filter(m -> "Y".equalsIgnoreCase(m.getActiveYn()))
                .isPresent();

        if (!alreadyExists) {
            LeagueMember member = new LeagueMember();
            member.setLeagueId(id);
            member.setUserId(user.getId());
            member.setRole("PLAYER");

            leagueMemberRepository.save(member);
        }

        return "redirect:/leagues/" + id;
    }

    private String generateLeagueCode() {
        return "L-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private Long currentUserId(HttpSession session) {
        return Long.valueOf(String.valueOf(session.getAttribute("USER_ID")));
    }

    private boolean isAdmin(HttpSession session) {
        String role = String.valueOf(session.getAttribute("ROLE"));
        return "ADMIN".equalsIgnoreCase(role);
    }

    private boolean canEditLeague(HttpSession session, League league) {
        Long userId = currentUserId(session);

        return userId.equals(league.getOwnerUserId()) || isAdmin(session);
    }

    private void addUserInfo(HttpSession session, Model model) {
        model.addAttribute("fullName", session.getAttribute("FULL_NAME"));
        model.addAttribute("role", session.getAttribute("ROLE"));
    }
}