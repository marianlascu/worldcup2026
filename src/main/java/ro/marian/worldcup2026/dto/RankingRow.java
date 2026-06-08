package ro.marian.worldcup2026.dto;

public class RankingRow {

    private Long userId;
    private String username;
    private String fullName;

    private int predictionsCount;
    private int exactScoreCount;
    private int resultCount;
    private int points;

    public RankingRow(Long userId,
                      String username,
                      String fullName,
                      int predictionsCount,
                      int exactScoreCount,
                      int resultCount,
                      int points) {

        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.predictionsCount = predictionsCount;
        this.exactScoreCount = exactScoreCount;
        this.resultCount = resultCount;
        this.points = points;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public int getPredictionsCount() {
        return predictionsCount;
    }

    public int getExactScoreCount() {
        return exactScoreCount;
    }

    public int getResultCount() {
        return resultCount;
    }

    public int getPoints() {
        return points;
    }
}