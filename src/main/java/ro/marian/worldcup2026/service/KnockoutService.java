package ro.marian.worldcup2026.service;

import org.springframework.stereotype.Service;
import ro.marian.worldcup2026.dto.GroupStanding;
import ro.marian.worldcup2026.dto.GroupStandingRow;
import ro.marian.worldcup2026.dto.KnockoutMatchView;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.repository.MatchGameRepository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KnockoutService {

    private final MatchGameRepository matchGameRepository;
    private final StandingService standingService;

    public KnockoutService(MatchGameRepository matchGameRepository,
                           StandingService standingService) {
        this.matchGameRepository = matchGameRepository;
        this.standingService = standingService;
    }

    public List<KnockoutMatchView> buildKnockoutMatches(Long tournamentId) {

        GroupLookup lookup = buildGroupLookup(tournamentId);

        return matchGameRepository.findByTournamentIdOrderByKickoffAtAscMatchNoAsc(tournamentId)
                .stream()
                .filter(this::isKnockoutMatch)
                .map(match -> toView(match, lookup))
                .toList();
    }

    private GroupLookup buildGroupLookup(Long tournamentId) {

        GroupLookup lookup = new GroupLookup();

        List<GroupStanding> groups = standingService.buildGroupStandings(tournamentId);

        for (GroupStanding group : groups) {

            String groupCode = extractGroupCode(group.getName());

            if (groupCode.isBlank() || group.getRows() == null) {
                continue;
            }

            for (GroupStandingRow row : group.getRows()) {

                if (row.getTeamName() == null || row.getTeamName().isBlank()) {
                    continue;
                }

                int position = row.getPosition();

                lookup.positionMap.put(position + groupCode, row.getTeamName());

                if (position == 3) {
                    lookup.thirdPlaceRows.put(groupCode, row);
                }
            }
        }

        return lookup;
    }

    private KnockoutMatchView toView(MatchGame match, GroupLookup lookup) {

        String rawTeamA = safe(match.getTeamA());
        String rawTeamB = safe(match.getTeamB());

        KnockoutMatchView view = new KnockoutMatchView();

        view.setId(match.getId());
        view.setMatchNo(match.getMatchNo());
        view.setStage(match.getStage());

        view.setRawTeamA(rawTeamA);
        view.setRawTeamB(rawTeamB);

        view.setTeamA(resolveTeam(rawTeamA, lookup));
        view.setTeamB(resolveTeam(rawTeamB, lookup));

        view.setScoreA(match.getScoreA());
        view.setScoreB(match.getScoreB());

        view.setKickoffAt(match.getKickoffAt());
        view.setVenueCity(match.getVenueCity());

        return view;
    }

    private boolean isKnockoutMatch(MatchGame match) {

        String stage = safe(match.getStage()).toUpperCase();

        return !stage.isBlank() && !"GROUP".equals(stage);
    }

    private String resolveTeam(String rawValue, GroupLookup lookup) {

        String value = safe(rawValue);

        if (value.isBlank()) {
            return value;
        }

        String directKey = normalizeWinnerRunnerPlaceholderToKey(value);

        if (directKey != null && lookup.positionMap.containsKey(directKey)) {
            return lookup.positionMap.get(directKey);
        }

        String thirdPlaceTeam = resolveThirdPlace(value, lookup);

        if (thirdPlaceTeam != null) {
            return thirdPlaceTeam;
        }

        return value;
    }

    private String normalizeWinnerRunnerPlaceholderToKey(String value) {

        String v = safe(value)
                .replace("_", " ")
                .replace("-", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String lower = v.toLowerCase();

        Matcher winner1 = Pattern.compile("(?i)^winner\\s+group\\s+([a-h])$").matcher(v);
        if (winner1.find()) {
            return "1" + winner1.group(1).toUpperCase();
        }

        Matcher winner2 = Pattern.compile("(?i)^group\\s+([a-h])\\s+winners?$").matcher(v);
        if (winner2.find()) {
            return "1" + winner2.group(1).toUpperCase();
        }

        Matcher runner1 = Pattern.compile("(?i)^runner\\s+up\\s+group\\s+([a-h])$").matcher(v);
        if (runner1.find()) {
            return "2" + runner1.group(1).toUpperCase();
        }

        Matcher runner2 = Pattern.compile("(?i)^group\\s+([a-h])\\s+runners?\\s+up$").matcher(v);
        if (runner2.find()) {
            return "2" + runner2.group(1).toUpperCase();
        }

        if (lower.matches("^[12][a-h]$")) {
            return v.toUpperCase();
        }

        if (lower.matches("^[12] [a-h]$")) {
            return v.replace(" ", "").toUpperCase();
        }

        return null;
    }

    private String resolveThirdPlace(String value, GroupLookup lookup) {

        String v = safe(value)
                .replace("_", " ")
                .replace("-", " ")
                .replaceAll("\\s+", " ")
                .trim();

        Matcher matcher = Pattern.compile("(?i)^group\\s+([a-h](?:/[a-h])*)\\s+third\\s+place$").matcher(v);

        if (!matcher.find()) {
            return null;
        }

        String groupList = matcher.group(1).toUpperCase();

        List<String> groupCodes = Arrays.stream(groupList.split("/"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        return groupCodes.stream()
                .map(lookup.thirdPlaceRows::get)
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt(GroupStandingRow::getPoints).reversed()
                        .thenComparing(Comparator.comparingInt(GroupStandingRow::getGoalsDiff).reversed())
                        .thenComparing(Comparator.comparingInt(GroupStandingRow::getGoalsScored).reversed())
                        .thenComparing(GroupStandingRow::getTeamName))
                .map(GroupStandingRow::getTeamName)
                .findFirst()
                .orElse(value);
    }

    private String extractGroupCode(String groupName) {

        String g = safe(groupName);

        if (g.toLowerCase().startsWith("group ")) {
            g = g.substring(6).trim();
        }

        if (g.isBlank()) {
            return "";
        }

        return g.substring(0, 1).toUpperCase();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static class GroupLookup {
        private final Map<String, String> positionMap = new HashMap<>();
        private final Map<String, GroupStandingRow> thirdPlaceRows = new HashMap<>();
    }
}