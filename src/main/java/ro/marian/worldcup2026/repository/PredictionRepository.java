package ro.marian.worldcup2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.marian.worldcup2026.model.Prediction;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Optional<Prediction> findByLeagueIdAndUserIdAndMatchId(Long leagueId, Long userId, Long matchId);

    List<Prediction> findByLeagueIdAndUserIdOrderByMatchIdAsc(Long leagueId, Long userId);

    List<Prediction> findByLeagueIdOrderByUserIdAscMatchIdAsc(Long leagueId);

    List<Prediction> findByLeagueIdAndMatchId(Long leagueId, Long matchId);
}