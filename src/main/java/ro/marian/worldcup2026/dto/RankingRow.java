package ro.marian.worldcup2026.dto;

public class RankingRow {

    private Long userId;
    private String username;
    private String fullName;

    private int predictions;
    private int exactScores;
    private int correctResults;
    private int points;

    public RankingRow() {
    }

    public RankingRow(Long userId,
                      String username,
                      String fullName,
                      int predictions,
                      int exactScores,
                      int correctResults,
                      int points) {

        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.predictions = predictions;
        this.exactScores = exactScores;
        this.correctResults = correctResults;
        this.points = points;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getPredictions() {
        return predictions;
    }

    public void setPredictions(int predictions) {
        this.predictions = predictions;
    }

    public int getExactScores() {
        return exactScores;
    }

    public void setExactScores(int exactScores) {
        this.exactScores = exactScores;
    }

    public int getCorrectResults() {
        return correctResults;
    }

    public void setCorrectResults(int correctResults) {
        this.correctResults = correctResults;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    public String getPlayer() {
        return getFullName();
    }

    public void setPlayer(String player) {
        this.fullName = player;
        this.username = player;
    }    
    
    public int getPredictionsCount() {
        return predictions;
    }

    public void setPredictionsCount(int predictionsCount) {
        this.predictions = predictionsCount;
    }

    public int getExactScoreCount() {
        return exactScores;
    }

    public void setExactScoreCount(int exactScoreCount) {
        this.exactScores = exactScoreCount;
    }

    public int getResultCount() {
        return correctResults;
    }

    public void setResultCount(int resultCount) {
        this.correctResults = resultCount;
    }    
    
}