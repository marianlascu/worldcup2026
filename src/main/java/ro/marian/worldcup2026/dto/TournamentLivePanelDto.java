package ro.marian.worldcup2026.dto;

import java.util.ArrayList;
import java.util.List;

public class TournamentLivePanelDto {

    private String tournamentName;

    private List<LivePanelMatchRow> pastMatches = new ArrayList<>();
    private LivePanelMatchRow focusMatch;
    private List<LivePanelMatchRow> nextMatches = new ArrayList<>();

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public List<LivePanelMatchRow> getPastMatches() {
        return pastMatches;
    }

    public void setPastMatches(List<LivePanelMatchRow> pastMatches) {
        this.pastMatches = pastMatches;
    }

    public LivePanelMatchRow getFocusMatch() {
        return focusMatch;
    }

    public void setFocusMatch(LivePanelMatchRow focusMatch) {
        this.focusMatch = focusMatch;
    }

    public List<LivePanelMatchRow> getNextMatches() {
        return nextMatches;
    }

    public void setNextMatches(List<LivePanelMatchRow> nextMatches) {
        this.nextMatches = nextMatches;
    }

    public boolean isEmpty() {
        return focusMatch == null
                && (pastMatches == null || pastMatches.isEmpty())
                && (nextMatches == null || nextMatches.isEmpty());
    }
}