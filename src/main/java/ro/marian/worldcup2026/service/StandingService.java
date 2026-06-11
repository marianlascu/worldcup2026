package ro.marian.worldcup2026.service;

import org.springframework.stereotype.Service;
import ro.marian.worldcup2026.dto.GroupStanding;
import ro.marian.worldcup2026.dto.GroupStandingRow;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.repository.MatchGameRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StandingService {

    private final MatchGameRepository matchGameRepository;

    public StandingService(MatchGameRepository matchGameRepository) {
        this.matchGameRepository = matchGameRepository;
    }

    public List<GroupStanding> buildGroupStandings(Long tournamentId) {

        List<MatchGame> matches =
                matchGameRepository.findByTournamentIdOrderByKickoffAtAscMatchNoAsc(tournamentId);

        Map<String, Map<String, GroupStandingRow>> groups = new LinkedHashMap<>();

        // 1. INIT: every team appears with zero statistics
        for (MatchGame match : matches) {

            if (match.getGroupName() == null || match.getGroupName().isBlank()) {
                continue;
            }

            String groupName = normalizeGroupName(match.getGroupName());

            groups.putIfAbsent(groupName, new LinkedHashMap<>());

            addTeamIfMissing(groups.get(groupName), match.getTeamA());
            addTeamIfMissing(groups.get(groupName), match.getTeamB());
        }

        // 2. CALC: only completed matches update standings
        for (MatchGame match : matches) {

            if (match.getGroupName() == null || match.getGroupName().isBlank()) {
                continue;
            }

            if (match.getScoreA() == null || match.getScoreB() == null) {
                continue;
            }

            String groupName = normalizeGroupName(match.getGroupName());

            Map<String, GroupStandingRow> groupRows = groups.get(groupName);

            if (groupRows == null) {
                continue;
            }

            GroupStandingRow teamA = groupRows.get(match.getTeamA());
            GroupStandingRow teamB = groupRows.get(match.getTeamB());

            if (teamA == null || teamB == null) {
                continue;
            }

            teamA.addMatch(match.getScoreA(), match.getScoreB());
            teamB.addMatch(match.getScoreB(), match.getScoreA());
        }

        // 3. Sort rows and assign positions
        List<GroupStanding> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, GroupStandingRow>> entry : groups.entrySet()) {

            List<GroupStandingRow> rows = new ArrayList<>(entry.getValue().values());

            rows.sort(
                    Comparator.comparingInt(GroupStandingRow::getPoints).reversed()
                            .thenComparing(Comparator.comparingInt(GroupStandingRow::getGoalsDiff).reversed())
                            .thenComparing(Comparator.comparingInt(GroupStandingRow::getGoalsScored).reversed())
                            .thenComparing(GroupStandingRow::getTeamName)
            );

            int position = 1;
            for (GroupStandingRow row : rows) {
                row.setPosition(position++);
            }

            result.add(new GroupStanding(entry.getKey(), rows));
        }

        return result;
    }

    private void addTeamIfMissing(Map<String, GroupStandingRow> groupRows, String teamName) {

        if (teamName == null || teamName.isBlank()) {
            return;
        }

        GroupStandingRow row = groupRows.get(teamName);

        if (row == null) {
            row = new GroupStandingRow(teamName);
            row.setFlagEmoji(flagForTeam(teamName));
            groupRows.put(teamName, row);
        }
    }

    private String normalizeGroupName(String groupName) {

        String g = groupName.trim();

        if (g.toLowerCase().startsWith("group ")) {
            return g;
        }

        return "Group " + g;
    }

    private String flagForTeam(String teamName) {

        if (teamName == null) {
            return "";
        }

        return switch (teamName.trim()) {
            case "Argentina" -> "🇦🇷";
            case "Australia" -> "🇦🇺";
            case "Austria" -> "🇦🇹";
            case "Belgium" -> "🇧🇪";
            case "Brazil" -> "🇧🇷";
            case "Cameroon" -> "🇨🇲";
            case "Canada" -> "🇨🇦";
            case "Chile" -> "🇨🇱";
            case "Colombia" -> "🇨🇴";
            case "Costa Rica" -> "🇨🇷";
            case "Croatia" -> "🇭🇷";
            case "Czech Republic" -> "🇨🇿";
            case "Denmark" -> "🇩🇰";
            case "Ecuador" -> "🇪🇨";
            case "Egypt" -> "🇪🇬";
            case "England" -> "🏴󠁧󠁢󠁥󠁮󠁧󠁿";
            case "France" -> "🇫🇷";
            case "Germany" -> "🇩🇪";
            case "Ghana" -> "🇬🇭";
            case "Iran" -> "🇮🇷";
            case "Italy" -> "🇮🇹";
            case "Japan" -> "🇯🇵";
            case "Mexico" -> "🇲🇽";
            case "Morocco" -> "🇲🇦";
            case "Netherlands" -> "🇳🇱";
            case "New Zealand" -> "🇳🇿";
            case "Nigeria" -> "🇳🇬";
            case "Norway" -> "🇳🇴";
            case "Paraguay" -> "🇵🇾";
            case "Peru" -> "🇵🇪";
            case "Poland" -> "🇵🇱";
            case "Portugal" -> "🇵🇹";
            case "Qatar" -> "🇶🇦";
            case "Saudi Arabia" -> "🇸🇦";
            case "Scotland" -> "🏴󠁧󠁢󠁳󠁣󠁴󠁿";
            case "Senegal" -> "🇸🇳";
            case "Serbia" -> "🇷🇸";
            case "South Africa" -> "🇿🇦";
            case "South Korea" -> "🇰🇷";
            case "Spain" -> "🇪🇸";
            case "Sweden" -> "🇸🇪";
            case "Switzerland" -> "🇨🇭";
            case "Tunisia" -> "🇹🇳";
            case "Turkey" -> "🇹🇷";
            case "Ukraine" -> "🇺🇦";
            case "Uruguay" -> "🇺🇾";
            case "USA", "United States" -> "🇺🇸";
            case "Wales" -> "🏴󠁧󠁢󠁷󠁬󠁳󠁿";
            case "Bosnia & Herzegovina",
                 "Bosnia-Herzegovina" -> "🇧🇦";
            case "Cape Verde",
                 "Cape Verde Islands" -> "🇨🇻";
            case "Curacao",
                 "Curaçao" -> "🇨🇼";
            case "DR Congo",
                 "Congo DR" -> "🇨🇩";
            case "Ivory Coast",
                 "Côte d'Ivoire" -> "🇨🇮";    
            case "Algeria" -> "🇩🇿";

            case "Haiti" -> "🇭🇹";

            case "Iraq" -> "🇮🇶";

            case "Jordan" -> "🇯🇴";

            case "Panama" -> "🇵🇦";

            case "Uzbekistan" -> "🇺🇿";                
            default -> "⚽";
        };
    }
}