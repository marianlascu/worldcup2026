package ro.marian.worldcup2026.dto;

public class GroupStandingRow {

    private int position;
    private String teamName;
    private String flagEmoji;

    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsScored;
    private int goalsAgainst;
    private int goalsDiff;
    private int points;

    public GroupStandingRow(String teamName) {
        this.teamName = teamName;
    }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getFlagEmoji() { return flagEmoji; }
    public void setFlagEmoji(String flagEmoji) { this.flagEmoji = flagEmoji; }

    public int getPlayed() { return played; }
    public void setPlayed(int played) { this.played = played; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getGoalsScored() { return goalsScored; }
    public void setGoalsScored(int goalsScored) { this.goalsScored = goalsScored; }

    public int getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }

    public int getGoalsDiff() { return goalsDiff; }
    public void setGoalsDiff(int goalsDiff) { this.goalsDiff = goalsDiff; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public void addMatch(int gf, int ga) {
        played++;
        goalsScored += gf;
        goalsAgainst += ga;
        goalsDiff = goalsScored - goalsAgainst;

        if (gf > ga) {
            wins++;
            points += 3;
        } else if (gf == ga) {
            draws++;
            points += 1;
        } else {
            losses++;
        }
    }
}