package ro.marian.worldcup2026.dto;

public class PredictionMatrixCell {

    private String value;      // 2-1, P, X
    private Integer points;    // 3, 1, 0 sau null
    private String cssClass;   // pred-3, pred-1, pred-0, pred-hidden, pred-missing

    public PredictionMatrixCell() {
    }

    public PredictionMatrixCell(String value, Integer points, String cssClass) {
        this.value = value;
        this.points = points;
        this.cssClass = cssClass;
    }

    public String getValue() {
        return value;
    }

    public Integer getPoints() {
        return points;
    }

    public String getCssClass() {
        return cssClass;
    }
}