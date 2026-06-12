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
         * Last games:
         * luam ultimele 5 meciuri terminate, dar le afisam cronologic crescator.
         * Exemplu: daca ultimele doua sunt 1 si 2, afisam 1, 2, nu 2, 1.
         */
        List<MatchGame> finished = matches.stream()
                .filter(m -> m.getKickoffAt() != null)
                .filter(m -> !m.getKickoffAt().isAfter(now))
                .filter(m -> m.getScoreA() != null && m.getScoreB() != null)
                .sorted(Comparator.comparing(MatchGame::getKickoffAt).reversed())
                .limit(5)
                .sorted(Comparator.comparing(MatchGame::getKickoffAt)
                        .thenComparing(MatchGame::getMatchNo))
                .toList();

        dto.setPastMatches(
                finished.stream()
                        .map(m -> toRow(m, false, true))
                        .toList()
        );

        /*
         * Focus match:
         * - daca exista meci inceput si fara scor final => LIVE NOW
         * - altfel primul meci viitor => NEXT MATCH
         */
        MatchGame liveMatch = matches.stream()
                .filter(m -> m.getKickoffAt() != null)
                .filter(m -> !m.getKickoffAt().isAfter(now))
                .filter(m -> m.getScoreA() == null || m.getScoreB() == null)
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

    private String compactStage(MatchGame match) {
        if (match.getGroupName() != null && !match.getGroupName().isBlank()) {
            return match.getGroupName();
        }

        return match.getStage() == null ? "" : match.getStage();
    }
}