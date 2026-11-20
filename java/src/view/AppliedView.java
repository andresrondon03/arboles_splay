package view;

import model.ScenarioResult;

import java.util.List;

/** VISTA del ejemplo aplicado: caché y benchmark. Solo formatea datos ya calculados. */
public class AppliedView {

    private static final int WIDTH = 86;

    // ---- General ----

    public void showSection(String title) {
        System.out.println("=== " + title + " ===");
    }

    public void showBlankLine() {
        System.out.println();
    }

    public void showMessage(String text) {
        System.out.println(text);
    }

    // ---- Caché ----

    public void showPut(String key, String rootKey) {
        System.out.printf("put(%-8s) -> raíz = %s%n", key, rootKey);
    }

    public void showRootAfterQueries(List<String> queried, String rootKey) {
        System.out.println("Tras consultar " + String.join(", ", queried) + " -> raíz = " + rootKey);
    }

    public void showStillCached(String key, boolean cached) {
        System.out.println("¿Sigue " + key + " en caché? " + (cached ? "sí" : "no (fue expulsada)"));
    }

    public void showStats(long hits, long misses, long evictions, double hitRate) {
        System.out.println("Estadísticas: " + formatStats(hits, misses, evictions, hitRate));
    }

    public void showProgress(int requests, String rootKey, long hits, long misses,
                             long evictions, double hitRate) {
        System.out.printf("Tras %4d peticiones -> raíz = %-14s | %s%n",
                requests, rootKey, formatStats(hits, misses, evictions, hitRate));
    }

    public void showLevels(List<List<String>> levels) {
        for (int depth = 0; depth < levels.size(); depth++) {
            System.out.println("  Nivel " + depth + ": " + levels.get(depth));
        }
    }

    private String formatStats(long hits, long misses, long evictions, double hitRate) {
        return String.format("aciertos=%d | fallos=%d | expulsiones=%d | tasa de acierto=%.1f%%",
                hits, misses, evictions, hitRate * 100);
    }

    // ---- Benchmark ----

    public void showBenchmarkHeader(int n, int accesses) {
        System.out.printf("Árbol con N = %d claves | %d accesos por escenario%n", n, accesses);
        System.out.println("(comp = comparaciones promedio por acceso)\n");
    }

    public void showBenchmarkTableHeader() {
        System.out.printf("%-26s | %9s | %9s | %8s | %8s | %7s%n",
                "Escenario", "BST (ms)", "Splay(ms)", "BST comp", "Spl comp", "BST/Spl");
        showSeparator();
    }

    public void showSeparator() {
        System.out.println("-".repeat(WIDTH));
    }

    public void showResult(ScenarioResult r) {
        System.out.printf("%-26s | %9.1f | %9.1f | %8.2f | %8.2f | %6.2fx%n",
                r.name(), r.bstMs(), r.splayMs(), r.bstAvgComparisons(),
                r.splayAvgComparisons(), r.speedup());
    }

    public void showBenchmarkConclusion() {
        System.out.println("Conclusión: medido en comparaciones, el Splay gana con localidad de");
        System.out.println("referencia (A, B, C) y no gana en D, donde los accesos no tienen patrón.");
        System.out.println("El tiempo depende del lenguaje y su entorno: el costo de las rotaciones pesa.");
    }
}
