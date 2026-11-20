package model;

/** MODELO (objeto de datos inmutable): resultado de un escenario del benchmark. */
public class ScenarioResult {

    private final String name;
    private final double bstMs;
    private final double splayMs;
    private final double bstAvgComparisons;
    private final double splayAvgComparisons;

    public ScenarioResult(String name, double bstMs, double splayMs,
                          double bstAvgComparisons, double splayAvgComparisons) {
        this.name = name;
        this.bstMs = bstMs;
        this.splayMs = splayMs;
        this.bstAvgComparisons = bstAvgComparisons;
        this.splayAvgComparisons = splayAvgComparisons;
    }

    public String name() {
        return name;
    }

    public double bstMs() {
        return bstMs;
    }

    public double splayMs() {
        return splayMs;
    }

    public double bstAvgComparisons() {
        return bstAvgComparisons;
    }

    public double splayAvgComparisons() {
        return splayAvgComparisons;
    }

    public double speedup() {
        return bstMs / splayMs;
    }
}
