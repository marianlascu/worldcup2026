package ro.marian.worldcup2026.dto;

import java.util.List;

public class GroupStanding {

    private String name;
    private List<GroupStandingRow> rows;

    public GroupStanding(String name, List<GroupStandingRow> rows) {
        this.name = name;
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public List<GroupStandingRow> getRows() {
        return rows;
    }
}