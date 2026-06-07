package ro.marian.worldcup2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.marian.worldcup2026.model.MatchGame;

import java.util.List;

public interface MatchGameRepository extends JpaRepository<MatchGame, Long> {

    List<MatchGame> findAllByOrderByKickoffAtAscMatchNoAsc();

    List<MatchGame> findByTournamentIdOrderByKickoffAtAscMatchNoAsc(Long tournamentId);
}