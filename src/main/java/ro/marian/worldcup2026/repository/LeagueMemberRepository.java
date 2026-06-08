package ro.marian.worldcup2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.marian.worldcup2026.model.LeagueMember;

import java.util.List;
import java.util.Optional;

public interface LeagueMemberRepository extends JpaRepository<LeagueMember, Long> {

    Optional<LeagueMember> findByLeagueIdAndUserId(Long leagueId, Long userId);

    List<LeagueMember> findByLeagueIdAndActiveYnOrderByJoinedAtAsc(Long leagueId, String activeYn);

    List<LeagueMember> findByUserIdAndActiveYnOrderByJoinedAtDesc(Long userId, String activeYn);
}