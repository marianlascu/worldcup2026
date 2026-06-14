package ro.marian.worldcup2026.service;

import org.springframework.stereotype.Service;
import ro.marian.worldcup2026.dto.LivePanelMatchRow;
import ro.marian.worldcup2026.dto.TournamentLivePanelDto;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.model.Tournament;
import ro.marian.worldcup2026.repository.MatchGameRepository;
import ro.marian.worldcup2026.repository.TournamentRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class TournamentLivePanelService {

    private final MatchGameRepository matchGameRepository;
    private final TournamentRepository tournamentRepository;

    public TournamentLivePanelService(MatchGameRepository matchGameRepository,
                                      TournamentRepository tournamentRepository) {
        this.matchGameRepository = matchGameRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public TournamentLivePanelDto build(Long tournamentId) {
        TournamentLivePanelDto dto = new TournamentLivePanelDto();

        if (tournamentId == null) {
            return dto;
        }

        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);

        if (tournament == null) {
            return dto;
        }

        dto.setTournamentName(tournament.getName());

        List<MatchGame> matches = matchGameRepository
                .findByTournamentIdOrderByKickoffAtAscMatchNoAsc(tournamentId);

        LocalDateTime now = LocalDateTime.now();

        /*
         * LAST GAMES:
         * Meciul ajunge aici doar daca este finalizat real.
         *
         * API:
         *   api_status = FINISHED
         *
         * Manual backup:
         *   score_source = MANUAL
         *   score_validated_yn = Y
         */
        List<MatchGame> finished = matches.stream()
                .filter(m -> m.getKickoffAt() != null)
                .filter(m -> !m.getKickoffAt().isAfter(now))
                .filter(this::isFinished)
                .sorted(Comparator.comparing(MatchGame::getKickoffAt).reversed())
                .limit(3)
                .sorted(Comparator.comparing(MatchGame::getKickoffAt)
                        .thenComparing(MatchGame::getMatchNo))
                .toList();

        dto.setPastMatches(
                finished.stream()
                        .map(m -> toRow(m, false, true))
                        .toList()
        );

        /*
         * FOCUS MATCH:
         * - daca a inceput si NU este finished => LIVE NOW
         * - chiar daca are scor API live, ramane live pana api_status = FINISHED
         * - altfel primul meci viitor => NEXT MATCH
         */
        MatchGame liveMatch = matches.stream()
                .filter(m -> m.getKickoffAt() != null)
                .filter(m -> !m.getKickoffAt().isAfter(now))
                .filter(m -> !isFinished(m))
                .min(Comparator.comparing(MatchGame::getKickoffAt)
                        .thenComparing(MatchGame::getMatchNo))
                .orElse(null);

        if (liveMatch != null) {
            dto.setFocusMatch(toRow(liveMatch, true, false));
        } else {
            MatchGame nextFocus = matches.stream()
                    .filter(m -> m.getKickoffAt() != null)
                    .filter(m -> m.getKickoffAt().isAfter(now))
                    .min(Comparator.comparing(MatchGame::getKickoffAt)
                            .thenComparing(MatchGame::getMatchNo))
                    .orElse(null);

            if (nextFocus != null) {
                dto.setFocusMatch(toRow(nextFocus, false, false));
            }
        }

        List<MatchGame> next = matches.stream()
                .filter(m -> m.getKickoffAt() != null)
                .filter(m -> m.getKickoffAt().isAfter(now))
                .filter(m -> dto.getFocusMatch() == null
                        || !m.getId().equals(dto.getFocusMatch().getMatchId()))
                .sorted(Comparator.comparing(MatchGame::getKickoffAt)
                        .thenComparing(MatchGame::getMatchNo))
                .limit(3)
                .toList();

        dto.setNextMatches(
                next.stream()
                        .map(m -> toRow(m, false, false))
                        .toList()
        );

        return dto;
    }

    private LivePanelMatchRow toRow(MatchGame match,
                                    boolean live,
                                    boolean finished) {

        return new LivePanelMatchRow(
                match.getId(),
                match.getMatchNo(),
                compactStage(match),
                match.getTeamA(),
                match.getTeamB(),
                match.getKickoffAt(),
                match.getScoreA(),
                match.getScoreB(),
                live,
                finished
        );
    }

    private boolean isFinished(MatchGame match) {
        if (match == null) {
            return false;
        }

        if ("FINISHED".equalsIgnoreCase(nullToEmpty(match.getApiStatus()))) {
            return true;
        }

        return "MANUAL".equalsIgnoreCase(nullToEmpty(match.getScoreSource()))
                && "Y".equalsIgnoreCase(nullToEmpty(match.getScoreValidatedYn()))
                && match.getScoreA() != null
                && match.getScoreB() != null;
    }

    private String compactStage(MatchGame match) {
        if (match.getGroupName() != null && !match.getGroupName().isBlank()) {
            return match.getGroupName();
        }

        return match.getStage() == null ? "" : match.getStage();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}