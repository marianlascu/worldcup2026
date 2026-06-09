package ro.marian.worldcup2026.service;

import org.springframework.stereotype.Service;
import ro.marian.worldcup2026.dto.PredictionMatrixCell;
import ro.marian.worldcup2026.dto.RankingPredictionMatrixRow;
import ro.marian.worldcup2026.dto.RankingRow;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.model.Prediction;
import ro.marian.worldcup2026.repository.MatchGameRepository;
import ro.marian.worldcup2026.repository.PredictionRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final PredictionRepository predictionRepository;
    private final MatchGameRepository matchGameRepository;

    public RankingService(PredictionRepository predictionRepository,
                          MatchGameRepository matchGameRepository) {
        this.predictionRepository = predictionRepository;
        this.matchGameRepository = matchGameRepository;
    }

    public List<RankingRow> buildRankings(Long leagueId, Long tournamentId) {
        List<MatchGame> matches = matchGameRepository
                .findByTournamentIdOrderByKickoffAtAscMatchNoAsc(tournamentId);

        Map<Long, MatchGame> matchMap = matches.stream()
                .filter(m -> m.getId() != null)
                .collect(Collectors.toMap(
                        MatchGame::getId,
                        Function.identity(),
                        (a, b) -> a
                ));

        List<Prediction> predictions = predictionRepository
                .findByLeagueIdOrderByUserIdAscMatchIdAsc(leagueId);

        Map<String, RankingRow> rankingMap = new LinkedHashMap<>();

        for (Prediction p : predictions) {
            String player = safePlayer(p.getUserId());

            RankingRow row = rankingMap.computeIfAbsent(player, k -> {
                RankingRow r = new RankingRow();
                r.setPlayer(k);
                return r;
            });

            row.setPredictions(row.getPredictions() + 1);

            MatchGame m = matchMap.get(p.getMatchId());

            if (m == null
                    || m.getScoreA() == null
                    || m.getScoreB() == null
                    || p.getPredictedScoreA() == null
                    || p.getPredictedScoreB() == null) {
                continue;
            }

            int points = calculatePoints(
                    p.getPredictedScoreA(),
                    p.getPredictedScoreB(),
                    m.getScoreA(),
                    m.getScoreB()
            );

            row.setPoints(row.getPoints() + points);

            if (points == 4) {
                row.setExact4(row.getExact4() + 1);
                row.setExactScores(row.getExactScores() + 1);
            } else if (points == 3) {
                row.setExact3(row.getExact3() + 1);
                row.setExactScores(row.getExactScores() + 1);
            } else if (points == 1) {
                row.setCorrectResults(row.getCorrectResults() + 1);
            }
        }

        return rankingMap.values()
                .stream()
                .sorted(
                        Comparator.comparing(RankingRow::getPoints).reversed()
                                .thenComparing(RankingRow::getExact4, Comparator.reverseOrder())
                                .thenComparing(RankingRow::getExact3, Comparator.reverseOrder())
                                .thenComparing(RankingRow::getCorrectResults, Comparator.reverseOrder())
                                .thenComparing(RankingRow::getPlayer, String.CASE_INSENSITIVE_ORDER)
                )
                .toList();
    }

    public List<RankingPredictionMatrixRow> buildPredictionMatrix(
            Long leagueId,
            Long tournamentId,
            Long currentUserId,
            boolean admin
    ) {
        List<MatchGame> matches = matchGameRepository
                .findByTournamentIdOrderByKickoffAtAscMatchNoAsc(tournamentId);

        List<Prediction> predictions = predictionRepository
                .findByLeagueIdOrderByUserIdAscMatchIdAsc(leagueId);

        List<Long> userIds = predictions.stream()
                .map(Prediction::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        Map<String, Prediction> predictionMap = predictions.stream()
                .filter(p -> p.getMatchId() != null)
                .filter(p -> p.getUserId() != null)
                .collect(Collectors.toMap(
                        p -> p.getMatchId() + "::" + p.getUserId(),
                        Function.identity(),
                        (a, b) -> b
                ));

        LocalDateTime now = LocalDateTime.now();
        List<RankingPredictionMatrixRow> rows = new ArrayList<>();

        for (MatchGame m : matches) {
            RankingPredictionMatrixRow row = new RankingPredictionMatrixRow();

            row.setMatchId(m.getId());
            row.setMatchNo(m.getMatchNo());
            row.setStage(m.getStage());
            row.setGroupName(m.getGroupName());
            row.setTeamA(m.getTeamA());
            row.setTeamB(m.getTeamB());
            row.setOfficialScoreA(m.getScoreA());
            row.setOfficialScoreB(m.getScoreB());
            row.setKickoffAt(m.getKickoffAt());

            boolean started = m.getKickoffAt() != null && !m.getKickoffAt().isAfter(now);
            row.setStarted(started);

            if (m.getScoreA() != null && m.getScoreB() != null) {
                row.setOfficialScore(m.getScoreA() + "-" + m.getScoreB());
            } else {
                row.setOfficialScore("-");
            }

            Map<String, PredictionMatrixCell> playerPredictions = new LinkedHashMap<>();

            for (Long userId : userIds) {
                Prediction p = predictionMap.get(m.getId() + "::" + userId);

                String visibleValue = "X";
                Integer cellPoints = null;
                String cellClass = "pred-missing";

                if (p != null
                        && p.getPredictedScoreA() != null
                        && p.getPredictedScoreB() != null) {

                    boolean ownPrediction = currentUserId != null && currentUserId.equals(userId);
                    boolean canSee = admin || started || ownPrediction;

                    if (canSee) {
                        visibleValue = p.getPredictedScoreA() + "-" + p.getPredictedScoreB();

                        if (m.getScoreA() != null && m.getScoreB() != null) {
                            cellPoints = calculatePoints(
                                    p.getPredictedScoreA(),
                                    p.getPredictedScoreB(),
                                    m.getScoreA(),
                                    m.getScoreB()
                            );

                            if (cellPoints == 4) {
                                cellClass = "pred-score-4";
                            } else if (cellPoints == 3) {
                                cellClass = "pred-score-3";
                            } else if (cellPoints == 1) {
                                cellClass = "pred-score-1";
                            } else {
                                cellClass = "pred-score-0";
                            }
                        } else {
                            cellClass = "pred-visible";
                        }

                    } else {
                        visibleValue = "P";
                        cellClass = "pred-hidden";
                    }
                }

                playerPredictions.put(
                        safePlayer(userId),
                        new PredictionMatrixCell(
                                visibleValue,
                                cellPoints,
                                cellClass
                        )
                );
            }

            row.setPlayerPredictions(playerPredictions);
            rows.add(row);
        }

        return rows;
    }

private int calculatePoints(int predA, int predB, int realA, int realB) {
    if (predA == realA && predB == realB) {
        int totalGoals = realA + realB;
        int pts = totalGoals > 3 ? 4 : 3;

        System.out.println("PREDICTOR SCORE DEBUG: exact "
                + predA + "-" + predB
                + " / real "
                + realA + "-" + realB
                + " / goals=" + totalGoals
                + " / points=" + pts);

        return pts;
    }

    int predSign = Integer.compare(predA, predB);
    int realSign = Integer.compare(realA, realB);

    int pts = predSign == realSign ? 1 : 0;

    System.out.println("PREDICTOR SCORE DEBUG: pred "
            + predA + "-" + predB
            + " / real "
            + realA + "-" + realB
            + " / points=" + pts);

    return pts;
}

    private String safePlayer(Long userId) {
        return userId == null ? "User ?" : "User " + userId;
    }
}