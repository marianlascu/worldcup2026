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

        boolean bothEmpty = scoreA == null && scoreB == null;
        boolean oneEmpty = scoreA == null || scoreB == null;

        if (bothEmpty) {

            match.setManualScoreA(null);
            match.setManualScoreB(null);

            if (!"API".equalsIgnoreCase(nullToEmpty(match.getScoreSource()))) {
                match.setScoreA(null);
                match.setScoreB(null);
                match.setScoreSource(null);
                match.setScoreValidatedYn("N");
                match.setScoreValidatedAt(null);
                match.setScoreValidatedBy(null);
            }

            match.setManualUpdatedAt(LocalDateTime.now());
            match.setManualUpdatedBy(username);

            matchGameRepository.save(match);
            return;
        }

        if (oneEmpty) {
            throw new IllegalArgumentException(
                    "Both scores must be provided or both left empty."
            );
        }

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

    /*
     * API auto-validate:
     * API scores become official immediately for rankings/tables/live panel.
     * The match is considered finished only by api_status = FINISHED,
     * not by the mere existence of scoreA/scoreB.
     */
    @Transactional
    public void applyApiScore(Long matchId) {

        MatchGame match = getMatch(matchId);

        if (match.getApiScoreA() == null || match.getApiScoreB() == null) {
            return;
        }

        match.setScoreA(match.getApiScoreA());
        match.setScoreB(match.getApiScoreB());

        match.setScoreSource("API");
        match.setScoreValidatedYn("Y");
        match.setScoreValidatedAt(LocalDateTime.now());
        match.setScoreValidatedBy("API");

        matchGameRepository.save(match);
    }

    /*
     * Kept only for backward compatibility / old controller calls.
     * Prefer applyApiScore() from API sync logic.
     */
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
        match.setScoreValidatedBy(username == null || username.isBlank() ? "API" : username);

        matchGameRepository.save(match);
    }

    public boolean isFinished(MatchGame match) {
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

    public boolean hasOfficialScore(MatchGame match) {
        return match != null
                && match.getScoreA() != null
                && match.getScoreB() != null
                && "Y".equalsIgnoreCase(nullToEmpty(match.getScoreValidatedYn()));
    }

    private MatchGame getMatch(Long matchId) {
        return matchGameRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}