package ro.marian.worldcup2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.marian.worldcup2026.model.WinnerPrediction;

import java.util.List;
import java.util.Optional;

public interface WinnerPredictionRepository
        extends JpaRepository<WinnerPrediction, Long> {

    Optional<WinnerPrediction> findByLeagueIdAndUserId(
            Long leagueId,
            Long userId
    );

    List<WinnerPrediction> findByLeagueIdOrderByTeamNameAsc(
            Long leagueId
    );

    List<WinnerPrediction> findByLeagueIdAndTournamentIdOrderByTeamNameAsc(
            Long leagueId,
            Long tournamentId
    );
}