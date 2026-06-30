package ro.marian.worldcup2026.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.marian.worldcup2026.dto.FootballDataMatchDto;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.repository.MatchGameRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchApiSyncService {

    private final FootballDataClient footballDataClient;
    private final MatchGameRepository matchGameRepository;

    public MatchApiSyncService(FootballDataClient footballDataClient,
                               MatchGameRepository matchGameRepository) {
        this.footballDataClient = footballDataClient;
        this.matchGameRepository = matchGameRepository;
    }

    @Transactional
    public int syncFootballDataMatches(Long tournamentId) {

        List<FootballDataMatchDto> apiMatches =
                footballDataClient.fetchCompetitionMatches();

        List<MatchGame> localMatches =
                matchGameRepository.findByTournamentIdOrderByKickoffAtAscMatchNoAsc(tournamentId);

        int matched = 0;
        int unmatched = 0;
        int updated = 0;

        System.out.println("[FOOTBALL-DATA SYNC] START tournamentId="
                + tournamentId
                + ", apiMatches="
                + apiMatches.size()
                + ", localMatches="
                + localMatches.size());

        for (FootballDataMatchDto apiMatch : apiMatches) {

            MatchGame localMatch = findLocalMatch(apiMatch, localMatches);

            if (localMatch == null) {

                boolean bothTeamsMissing =
                        apiMatch.getHomeTeamName() == null
                        && apiMatch.getAwayTeamName() == null;

                if (!bothTeamsMissing) {

                    unmatched++;

                    System.out.println("[FOOTBALL-DATA SYNC] UNMATCHED: "
                            + apiMatch.getHomeTeamName()
                            + " - "
                            + apiMatch.getAwayTeamName()
                            + " / status="
                            + apiMatch.getStatus()
                            + " / kickoff="
                            + apiMatch.getUtcKickoffAt());
                }

                continue;
            }

            matched++;

            boolean changed = applyApiData(localMatch, apiMatch);

            if (changed) {
                updated++;

                System.out.println("[FOOTBALL-DATA SYNC] UPDATED: #"
                        + localMatch.getMatchNo()
                        + " "
                        + localMatch.getTeamA()
                        + " - "
                        + localMatch.getTeamB()
                        + " / apiStatus="
                        + apiMatch.getStatus()
                        + " / apiScore="
                        + apiMatch.getHomeScore()
                        + "-"
                        + apiMatch.getAwayScore());
            }
        }

        System.out.println("[FOOTBALL-DATA SYNC] DONE tournamentId="
                + tournamentId
                + ", matched="
                + matched
                + ", unmatched="
                + unmatched
                + ", updated="
                + updated);

        return updated;
    }

    private MatchGame findLocalMatch(FootballDataMatchDto apiMatch,
                                     List<MatchGame> localMatches) {

        if (apiMatch.getExternalId() != null) {
            MatchGame byApiId = localMatches.stream()
                    .filter(m -> apiMatch.getExternalId().equals(m.getApiMatchId()))
                    .findFirst()
                    .orElse(null);

            if (byApiId != null) {
                return byApiId;
            }
        }

        if (apiMatch.getHomeTeamName() == null || apiMatch.getAwayTeamName() == null) {
            return null;
        }

        String apiHome = normalizeTeam(apiMatch.getHomeTeamName());
        String apiAway = normalizeTeam(apiMatch.getAwayTeamName());

        return localMatches.stream()
                .filter(m -> normalizeTeam(m.getTeamA()).equals(apiHome))
                .filter(m -> normalizeTeam(m.getTeamB()).equals(apiAway))
                .findFirst()
                .orElse(null);
    }

    private boolean applyApiData(MatchGame match,
                                 FootballDataMatchDto apiMatch) {

        boolean changed = false;

        if (apiMatch.getExternalId() != null
                && !apiMatch.getExternalId().equals(match.getApiMatchId())) {
            match.setApiMatchId(apiMatch.getExternalId());
            changed = true;
        }

        if (apiMatch.getUtcKickoffAt() != null
                && !apiMatch.getUtcKickoffAt().equals(match.getKickoffAt())) {
            match.setKickoffAt(apiMatch.getUtcKickoffAt());
            changed = true;
        }

        /*
         * API raw fields
         * Astea se pot actualiza oricand, inclusiv dupa validare manuala.
         */
        if (!equalsInteger(match.getApiScoreA(), apiMatch.getHomeScore())) {
            match.setApiScoreA(apiMatch.getHomeScore());
            changed = true;
        }

        if (!equalsInteger(match.getApiScoreB(), apiMatch.getAwayScore())) {
            match.setApiScoreB(apiMatch.getAwayScore());
            changed = true;
        }

        if (!equalsString(match.getApiStatus(), apiMatch.getStatus())) {
            match.setApiStatus(apiMatch.getStatus());
            changed = true;
        }

        /*
         * Daca scorul oficial a fost validat manual,
         * API-ul NU mai are voie sa suprascrie scoreA / scoreB.
         *
         * Important pentru knockout:
         * API poate returna scor dupa extra-time / penalty shootout,
         * dar regula predictorului este scorul dupa 90 minute.
         */
        boolean manuallyValidated =
                "MANUAL".equalsIgnoreCase(match.getScoreSource())
                        && "Y".equalsIgnoreCase(match.getScoreValidatedYn());

        if (!manuallyValidated
                && apiMatch.getHomeScore() != null
                && apiMatch.getAwayScore() != null) {

            boolean officialChanged = false;

            if (!equalsInteger(match.getScoreA(), apiMatch.getHomeScore())) {
                match.setScoreA(apiMatch.getHomeScore());
                changed = true;
                officialChanged = true;
            }

            if (!equalsInteger(match.getScoreB(), apiMatch.getAwayScore())) {
                match.setScoreB(apiMatch.getAwayScore());
                changed = true;
                officialChanged = true;
            }

            if (!equalsString(match.getScoreSource(), "API")) {
                match.setScoreSource("API");
                changed = true;
                officialChanged = true;
            }

            if (!equalsString(match.getScoreValidatedYn(), "Y")) {
                match.setScoreValidatedYn("Y");
                changed = true;
                officialChanged = true;
            }

            if (officialChanged) {
                match.setScoreValidatedAt(LocalDateTime.now());
                match.setScoreValidatedBy("API");
            }
        }

        if (changed) {
            match.setApiUpdatedAt(LocalDateTime.now());
            matchGameRepository.save(match);
        }

        return changed;
    }

    private String normalizeTeam(String value) {
        if (value == null) {
            return "";
        }

        String mapped = mapApiTeamName(value);

        return mapped
                .trim()
                .toLowerCase()
                .replace(" ", "")
                .replace("-", "")
                .replace(".", "")
                .replace("'", "")
                .replace("’", "")
                .replace("ç", "c")
                .replace("ã", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o");
    }

    private String mapApiTeamName(String value) {
        if (value == null) {
            return "";
        }

        return switch (value.trim()) {
            case "United States" -> "USA";
            case "Czechia" -> "Czech Republic";
            case "Bosnia-Herzegovina" -> "Bosnia & Herzegovina";
            case "Curaçao" -> "Curacao";
            case "Cape Verde Islands" -> "Cape Verde";
            case "Congo DR" -> "DR Congo";
            case "South Korea" -> "Korea Republic";
            case "Ivory Coast" -> "Côte d'Ivoire";
            case "Turkey" -> "Türkiye";
            default -> value;
        };
    }

    private boolean equalsInteger(Integer a, Integer b) {
        return a == null ? b == null : a.equals(b);
    }

    private boolean equalsString(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}