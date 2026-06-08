package ro.marian.worldcup2026.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.repository.MatchGameRepository;

import java.time.LocalDateTime;

@Service
public class MatchScoreService {

    private final MatchGameRepository matchGameRepository;

    public MatchScoreService(MatchGameRepository matchGameRepository) {
        this.matchGameRepository = matchGameRepository;
    }

    @Transactional
    public void saveManualScore(Long matchId,
                                Integer scoreA,
                                Integer scoreB,
                                String username) {

        MatchGame match = getMatch(matchId);

        match.setManualScoreA(scoreA);
        match.setManualScoreB(scoreB);
        match.setManualUpdatedAt(LocalDateTime.now());
        match.setManualUpdatedBy(username);

        matchGameRepository.save(match);
    }

    @Transactional
    public void validateManualScore(Long matchId,
                                    String username) {

        MatchGame match = getMatch(matchId);

        if (match.getManualScoreA() == null || match.getManualScoreB() == null) {
            throw new IllegalStateException("Manual score is incomplete");
        }

        match.setScoreA(match.getManualScoreA());
        match.setScoreB(match.getManualScoreB());

        match.setScoreSource("MANUAL");
        match.setScoreValidatedYn("Y");
        match.setScoreValidatedAt(LocalDateTime.now());
        match.setScoreValidatedBy(username);

        matchGameRepository.save(match);
    }

    @Transactional
    public void validateApiScore(Long matchId,
                                 String username) {

        MatchGame match = getMatch(matchId);

        if (match.getApiScoreA() == null || match.getApiScoreB() == null) {
            throw new IllegalStateException("API score is incomplete");
        }

        match.setScoreA(match.getApiScoreA());
        match.setScoreB(match.getApiScoreB());

        match.setScoreSource("API");
        match.setScoreValidatedYn("Y");
        match.setScoreValidatedAt(LocalDateTime.now());
        match.setScoreValidatedBy(username);

        matchGameRepository.save(match);
    }

    private MatchGame getMatch(Long matchId) {
        return matchGameRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));
    }
}