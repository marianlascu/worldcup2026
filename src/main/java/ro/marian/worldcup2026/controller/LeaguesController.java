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
import ro.marian.worldcup2026.dto.PredictionMatchRow;
import ro.marian.worldcup2026.repository.PredictionRepository;
import ro.marian.worldcup2026.repository.MatchGameRepository;
import ro.marian.worldcup2026.dto.RankingRow;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.model.Prediction;
import ro.marian.worldcup2026.dto.RankingPredictionMatrixRow;
import ro.marian.worldcup2026.dto.PredictionMatrixCell;
import ro.marian.worldcup2026.service.TournamentLivePanelService;
import ro.marian.worldcup2026.model.WinnerPrediction;
import ro.marian.worldcup2026.repository.WinnerPredictionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LeaguesController {

    private final LeagueRepository leagueRepository;
    private final LeagueMemberRepository leagueMemberRepository;
    private final TournamentRepository tournamentRepository;
    private final AppUserRepository appUserRepository;
    private final PredictionRepository predictionRepository;
    private final MatchGameRepository matchGameRepository;
    private final TournamentLivePanelService tournamentLivePanelService;
    private final WinnerPredictionRepository winnerPredictionRepository;

    public LeaguesController(
            LeagueRepository leagueRepository,
            LeagueMemberRepository leagueMemberRepository,
            TournamentRepository tournamentRepository,
            AppUserRepository appUserRepository,
            PredictionRepository predictionRepository,
            MatchGameRepository matchGameRepository,
            TournamentLivePanelService tournamentLivePanelService,
            WinnerPredictionRepository winnerPredictionRepository) {

        this.leagueRepository = leagueRepository;
        this.leagueMemberRepository = leagueMemberRepository;
        this.tournamentRepository = tournamentRepository;
        this.appUserRepository = appUserRepository;
        this.predictionRepository = predictionRepository;
        this.matchGameRepository = matchGameRepository;
        this.tournamentLivePanelService = tournamentLivePanelService;
        this.winnerPredictionRepository = winnerPredictionRepository;
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
    public String leagueOverview(@PathVariable Long id,
                                 HttpSession session,
                                 Model model) {

        String view = openLeagueTab(id, "overview", session, model);

        if (!"league-layout".equals(view)) {
            return view;
        }

        return view;
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
                            @RequestParam Long userIdToAdd,
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

        AppUser user = appUserRepository.findById(userIdToAdd).orElse(null);

        if (user == null) {
            return "redirect:/leagues/" + id;
        }

        LeagueMember existing = leagueMemberRepository
                .findByLeagueIdAndUserId(id, user.getId())
                .orElse(null);

        if (existing == null) {
            LeagueMember member = new LeagueMember();
            member.setLeagueId(id);
            member.setUserId(user.getId());
            member.setRole("PLAYER");
            member.setActiveYn("Y");

            leagueMemberRepository.save(member);
        } else {
            existing.setActiveYn("Y");

            if (existing.getRole() == null || existing.getRole().isBlank()) {
                existing.setRole("PLAYER");
            }

            leagueMemberRepository.save(existing);
        }

        return "redirect:/leagues/" + id;
    }

    @PostMapping("/leagues/{id}/members/{userId}/remove")
    public String removeMember(@PathVariable Long id,
                               @PathVariable Long userId,
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

        if (userId.equals(league.getOwnerUserId())) {
            return "redirect:/leagues/" + id;
        }

        LeagueMember member = leagueMemberRepository
                .findByLeagueIdAndUserId(id, userId)
                .orElse(null);

        if (member != null) {
            member.setActiveYn("N");
            leagueMemberRepository.save(member);
        }

        return "redirect:/leagues/" + id;
    }
    
    @GetMapping("/leagues/{id}/members")
    public String leagueMembers(@PathVariable Long id,
                                HttpSession session,
                                Model model) {

        String view = openLeagueTab(id, "members", session, model);

        if (!"league-layout".equals(view)) {
            return view;
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

        model.addAttribute("members", memberRows);
        java.util.Set<Long> existingMemberUserIds = members.stream()
                .map(LeagueMember::getUserId)
                .collect(java.util.stream.Collectors.toSet());

        List<AppUser> availableUsers = appUserRepository.findAll()
                .stream()
                .filter(u -> !existingMemberUserIds.contains(u.getId()))
                .toList();

        model.addAttribute("allUsers", availableUsers);

        return view;
    }    
    
    @GetMapping("/leagues/{id}/predictions")
    public String leaguePredictions(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {

        String view = openLeagueTab(id, "predictions", session, model);

        if (!"league-layout".equals(view)) {
            return view;
        }

        Long userId = currentUserId(session);

        League league = (League) model.getAttribute("league");

        List<MatchGame> matches =
                matchGameRepository.findByTournamentIdOrderByKickoffAtAscMatchNoAsc(
                        league.getTournamentId()
                );

        Map<Long, Prediction> predictionsByMatchId =
                predictionRepository.findByLeagueIdAndUserIdOrderByMatchIdAsc(id, userId)
                        .stream()
                        .collect(Collectors.toMap(Prediction::getMatchId, p -> p));

        LocalDateTime now = LocalDateTime.now();

        boolean winnerLocked = matches.stream()
                .anyMatch(m -> m.getKickoffAt() != null
                        && !m.getKickoffAt().isAfter(now));

        List<PredictionMatchRow> predictionRows = matches.stream()
                .map(m -> {
                    Prediction p = predictionsByMatchId.get(m.getId());

                    boolean locked = m.getKickoffAt() != null
                            && !m.getKickoffAt().isAfter(now);

                    return new PredictionMatchRow(
                            m.getId(),
                            m.getMatchNo(),
                            m.getStage(),
                            m.getGroupName(),
                            m.getTeamA(),
                            m.getTeamB(),
                            m.getKickoffAt(),
                            m.getVenueCity(),
                            m.getScoreA(),
                            m.getScoreB(),
                            p == null ? null : p.getPredictedScoreA(),
                            p == null ? null : p.getPredictedScoreB(),
                            locked
                    );
                })
                .toList();

        List<String> winnerTeams = matches.stream()
                .flatMap(m -> java.util.stream.Stream.of(m.getTeamA(), m.getTeamB()))
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .filter(s -> !isPlaceholderTeamName(s))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        WinnerPrediction winnerPrediction = winnerPredictionRepository
                .findByLeagueIdAndUserId(id, userId)
                .orElse(null);

        model.addAttribute("predictionRows", predictionRows);
        model.addAttribute("winnerTeams", winnerTeams);
        model.addAttribute("winnerPrediction", winnerPrediction);
        model.addAttribute("winnerLocked", winnerLocked);
        model.addAttribute(
                "winnerTeamName",
                winnerPrediction == null ? "" : winnerPrediction.getTeamName()
        );

        return view;
    }

    @PostMapping("/leagues/{id}/predictions/save")
    public String savePredictions(@PathVariable Long id,
                                  @RequestParam List<Long> matchId,
                                  @RequestParam Map<String, String> params,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

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

        LocalDateTime now = LocalDateTime.now();

        int savedCount = 0;

        for (Long mid : matchId) {

            Integer scoreA = parseInteger(params.get("scoreA_" + mid));
            Integer scoreB = parseInteger(params.get("scoreB_" + mid));

            if (scoreA == null && scoreB == null) {
                continue;
            }

            if (scoreA == null || scoreB == null) {
                continue;
            }

            MatchGame match = matchGameRepository.findById(mid).orElse(null);

            if (match == null) {
                continue;
            }

            boolean locked = match.getKickoffAt() != null
                    && !match.getKickoffAt().isAfter(now);

            if (locked) {
                continue;
            }

            Prediction prediction = predictionRepository
                    .findByLeagueIdAndUserIdAndMatchId(id, userId, mid)
                    .orElseGet(() -> {
                        Prediction p = new Prediction();
                        p.setLeagueId(id);
                        p.setUserId(userId);
                        p.setMatchId(mid);
                        return p;
                    });

            prediction.setPredictedScoreA(scoreA);
            prediction.setPredictedScoreB(scoreB);

            predictionRepository.save(prediction);

            savedCount++;
        }

        redirectAttributes.addFlashAttribute(
                "predictionSaveMessage",
                savedCount + " predictions saved!"
        );

        return "redirect:/leagues/" + id + "/predictions";
    }    
    
    @GetMapping("/leagues/{id}/rankings")
    public String leagueRankings(@PathVariable Long id,
                                 HttpSession session,
                                 Model model) {

        String view = openLeagueTab(id, "rankings", session, model);

        if (!"league-layout".equals(view)) {
            return view;
        }

        List<LeagueMember> members =
                leagueMemberRepository.findByLeagueIdAndActiveYnOrderByJoinedAtAsc(id, "Y");

        Map<Long, AppUser> usersById = appUserRepository.findAllById(
                members.stream()
                        .map(LeagueMember::getUserId)
                        .toList()
        ).stream().collect(Collectors.toMap(AppUser::getId, u -> u));

        List<Prediction> predictions =
                predictionRepository.findByLeagueIdOrderByUserIdAscMatchIdAsc(id);

        Map<Long, MatchGame> matchesById = matchGameRepository.findAllById(
                predictions.stream()
                        .map(Prediction::getMatchId)
                        .toList()
        ).stream().collect(Collectors.toMap(MatchGame::getId, m -> m));

        List<RankingRow> rankings = members.stream()
                .map(member -> buildRankingRow(member, usersById, predictions, matchesById))
                .sorted(java.util.Comparator.comparingInt(RankingRow::getPoints).reversed()
                        .thenComparing(RankingRow::getFullName))
                .toList();

        model.addAttribute("rankings", rankings);
        League league = (League) model.getAttribute("league");

        List<MatchGame> tournamentMatches =
                matchGameRepository.findByTournamentIdOrderByKickoffAtAscMatchNoAsc(
                        league.getTournamentId()
                );

        List<RankingPredictionMatrixRow> predictionMatrix =
                buildPredictionMatrixRows(
                        members,
                        usersById,
                        predictions,
                        tournamentMatches,
                        currentUserId(session),
                        isAdmin(session)
                );

        model.addAttribute("predictionMatrix", predictionMatrix); 
        List<String> predictionMatrixPlayers = members.stream()
                .map(m -> {
                    AppUser user = usersById.get(m.getUserId());
                    return user == null ? "User " + m.getUserId() : user.getFullName();
                })
                .toList();

        model.addAttribute("predictionMatrixPlayers", predictionMatrixPlayers);    
        
        List<WinnerPrediction> winnerPredictions =
                winnerPredictionRepository.findByLeagueIdOrderByTeamNameAsc(id);

        List<WinnerPickRow> winnerPickRows = winnerPredictions.stream()
                .map(w -> {
                    AppUser user = usersById.get(w.getUserId());

                    String fullName = user == null
                            ? "User " + w.getUserId()
                            : user.getFullName();

                    return new WinnerPickRow(
                            fullName,
                            w.getTeamName()
                    );
                })
                .toList();

        model.addAttribute("winnerPickRows", winnerPickRows);       

        return view;
    }  
    
    @PostMapping("/leagues/{id}/winner/save")
    public String saveWinnerPrediction(@PathVariable Long id,
                                       @RequestParam String teamName,
                                       HttpSession session) {

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

        if (teamName == null || teamName.isBlank()) {
            return "redirect:/leagues/" + id + "/predictions";
        }

        WinnerPrediction winnerPrediction = winnerPredictionRepository
                .findByLeagueIdAndUserId(id, userId)
                .orElseGet(() -> {
                    WinnerPrediction wp = new WinnerPrediction();
                    wp.setLeagueId(id);
                    wp.setUserId(userId);
                    wp.setTournamentId(league.getTournamentId());
                    return wp;
                });

        winnerPrediction.setTeamName(teamName.trim());
        winnerPrediction.setTournamentId(league.getTournamentId());

        winnerPredictionRepository.save(winnerPrediction);

        return "redirect:/leagues/" + id + "/predictions";
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
    
    private String openLeagueTab(Long id,
                             String activeLeagueTab,
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

    addUserInfo(session, model);

    model.addAttribute("league", league);
    model.addAttribute(
            "livePanel",
            tournamentLivePanelService.build(league.getTournamentId())
    );    
    model.addAttribute("activeLeagueTab", activeLeagueTab);
    model.addAttribute("contentFragment", leagueContentFragmentFor(activeLeagueTab));
    model.addAttribute("canEditLeague", canEditLeague(session, league));

    return "league-layout";
    }
    
    private String leagueContentFragmentFor(String activeLeagueTab) {
        return switch (activeLeagueTab) {
            case "overview" -> "league-overview :: content";
            case "members" -> "league-members :: content";
            case "predictions" -> "league-predictions :: content";
            case "rankings" -> "league-rankings :: content";
            default -> "league-overview :: content";
        };
    }    
    
    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }    
    
    private RankingRow buildRankingRow(LeagueMember member,
                                       Map<Long, AppUser> usersById,
                                       List<Prediction> allPredictions,
                                       Map<Long, MatchGame> matchesById) {

        AppUser user = usersById.get(member.getUserId());

        int correctTotal = 0;
        int exact4 = 0;
        int exact3 = 0;
        int exactScoreCount = 0;
        int resultCount = 0;
        int points = 0;

        for (Prediction p : allPredictions) {

            if (!member.getUserId().equals(p.getUserId())) {
                continue;
            }

            MatchGame match = matchesById.get(p.getMatchId());

            if (match == null) {
                continue;
            }

            if (p.getPredictedScoreA() == null || p.getPredictedScoreB() == null) {
                continue;
            }

            if (match.getScoreA() == null || match.getScoreB() == null) {
                continue;
            }

            int gained = predictionPoints(
                    p.getPredictedScoreA(),
                    p.getPredictedScoreB(),
                    match.getScoreA(),
                    match.getScoreB()
            );

            if (gained <= 0) {
                continue;
            }

            correctTotal++;
            points += gained;

            if (gained == 4) {
                exact4++;
                exactScoreCount++;
            } else if (gained == 3) {
                exact3++;
                exactScoreCount++;
            } else if (gained == 1) {
                resultCount++;
            }
        }

        return new RankingRow(
                member.getUserId(),
                user == null ? "" : user.getUsername(),
                user == null ? "(missing user)" : user.getFullName(),
                correctTotal,
                exact4,
                exact3,
                exactScoreCount,
                resultCount,
                points
        );
    }

    private int resultSign(Integer a, Integer b) {
        return Integer.compare(a, b);
    }    
    
    private List<RankingPredictionMatrixRow> buildPredictionMatrixRows(
            List<LeagueMember> members,
            Map<Long, AppUser> usersById,
            List<Prediction> predictions,
            List<MatchGame> matches,
            Long currentUserId,
            boolean admin
    ) {
        Map<String, Prediction> predictionsByMatchAndUser = predictions.stream()
                .filter(p -> p.getMatchId() != null)
                .filter(p -> p.getUserId() != null)
                .collect(Collectors.toMap(
                        p -> p.getMatchId() + "::" + p.getUserId(),
                        p -> p,
                        (a, b) -> b
                ));

        LocalDateTime now = LocalDateTime.now();
        List<RankingPredictionMatrixRow> rows = new ArrayList<>();

        for (MatchGame match : matches) {
            RankingPredictionMatrixRow row = new RankingPredictionMatrixRow();

            row.setMatchId(match.getId());
            row.setMatchNo(match.getMatchNo());
            row.setStage(compactStage(match));
            row.setGroupName(match.getGroupName());
            row.setTeamA(match.getTeamA());
            row.setTeamB(match.getTeamB());
            row.setKickoffAt(match.getKickoffAt());

            row.setOfficialScoreA(match.getScoreA());
            row.setOfficialScoreB(match.getScoreB());

            if (match.getScoreA() != null && match.getScoreB() != null) {
                row.setOfficialScore(match.getScoreA() + "-" + match.getScoreB());
            } else {
                row.setOfficialScore("-");
            }

            boolean started = match.getKickoffAt() != null && !match.getKickoffAt().isAfter(now);
            row.setStarted(started);

            Map<String, PredictionMatrixCell> playerPredictions = new LinkedHashMap<>();

            for (LeagueMember member : members) {
                Long userId = member.getUserId();
                AppUser user = usersById.get(userId);

                String playerName = user == null
                        ? "User " + userId
                        : user.getFullName();

                Prediction prediction = predictionsByMatchAndUser.get(match.getId() + "::" + userId);

                PredictionMatrixCell cell;

                if (prediction == null
                        || prediction.getPredictedScoreA() == null
                        || prediction.getPredictedScoreB() == null) {

                    cell = new PredictionMatrixCell("X", null, "pred-missing");

                } else {
                    boolean ownPrediction = currentUserId != null && currentUserId.equals(userId);
                    boolean canSee = admin || started || ownPrediction;

                    if (canSee) {
                        String value = prediction.getPredictedScoreA()
                                + "-"
                                + prediction.getPredictedScoreB();

                        Integer points = null;
                        String cssClass = "pred-visible";

                        if (match.getScoreA() != null && match.getScoreB() != null) {
                            points = predictionPoints(
                                    prediction.getPredictedScoreA(),
                                    prediction.getPredictedScoreB(),
                                    match.getScoreA(),
                                    match.getScoreB()
                            );

                            cssClass = switch (points) {
                                case 4 -> "pred-score-4";
                                case 3 -> "pred-score-3";
                                case 1 -> "pred-score-1";
                                default -> "pred-score-0";
                            };
                        }

                        cell = new PredictionMatrixCell(value, points, cssClass);

                    } else {
                        cell = new PredictionMatrixCell("P", null, "pred-hidden");
                    }
                }

                playerPredictions.put(playerName, cell);
            }

            row.setPlayerPredictions(playerPredictions);
            rows.add(row);
        }

        return rows;
    }   
    
    private int predictionPoints(int predA, int predB, int realA, int realB) {
        if (predA == realA && predB == realB) {
            int totalGoals = realA + realB;
            return totalGoals > 3 ? 4 : 3;
        }

        int predSign = Integer.compare(predA, predB);
        int realSign = Integer.compare(realA, realB);

        return predSign == realSign ? 1 : 0;
    }    
    
    private String compactStage(MatchGame match) {
        if (match == null) {
            return "";
        }

        String stage = match.getStage() == null ? "" : match.getStage().trim();
        String groupName = match.getGroupName() == null ? "" : match.getGroupName().trim();

        if (!groupName.isBlank()) {
            return "Gr." + groupName;
        }

        String s = stage.toLowerCase();

        if (s.contains("round of 32") || s.contains("r32")) {
            return "R32";
        }

        if (s.contains("round of 16") || s.contains("r16")) {
            return "R16";
        }

        if (s.contains("quarter")) {
            return "QF";
        }

        if (s.contains("semi")) {
            return "SF";
        }

        if (s.contains("final")) {
            return "F";
        }

        if (s.contains("third")) {
            return "3rd";
        }

        return stage;
    }    
    
    private boolean isPlaceholderTeamName(String teamName) {
        if (teamName == null) {
            return true;
        }

        String s = teamName.trim().toLowerCase();

        if (s.isBlank()) {
            return true;
        }

        return s.contains("winner")
                || s.contains("runner")
                || s.contains("group")
                || s.contains("third")
                || s.contains("3rd")
                || s.matches(".*\\b[123][a-h]\\b.*")
                || s.matches(".*\\b[123]\\s*[a-h]\\b.*");
    } 
    
    public record WinnerPickRow(
            String fullName,
            String teamName
    ) {
    }    
}