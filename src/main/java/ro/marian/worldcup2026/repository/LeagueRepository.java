package ro.marian.worldcup2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.marian.worldcup2026.model.League;

import java.util.List;
import java.util.Optional;

public interface LeagueRepository extends JpaRepository<League, Long> {

    Optional<League> findByCode(String code);

    List<League> findByTournamentIdAndActiveYnOrderByNameAsc(Long tournamentId, String activeYn);

    List<League> findByOwnerUserIdAndActiveYnOrderByCreatedAtDesc(Long ownerUserId, String activeYn);
}