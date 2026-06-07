package ro.marian.worldcup2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.marian.worldcup2026.model.Tournament;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findAllByOrderByNameAsc();

    Optional<Tournament> findByCode(String code);
}