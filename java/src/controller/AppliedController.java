package controller;

import model.Lcg;
import model.LruCache;
import model.PlainBst;
import model.ScenarioResult;
import model.SearchTree;
import model.SplayTree;
import view.AppliedView;

import java.util.List;

/**
 * CONTROLADOR del ejemplo aplicado: simula una caché y ejecuta un benchmark pequeño.
 * Decide los escenarios, mide, y entrega los datos a la vista.
 */
public class AppliedController {

    private static final int N = 10_000;           // claves en el árbol del benchmark
    private static final int ACCESSES = 100_000;   // accesos por escenario
    private static final int HOT_SET = 10;         // tamaño del conjunto caliente

    private final AppliedView view;

    public AppliedController(AppliedView view) {
        this.view = view;
    }

    public void runDemo() {
        cacheStepByStep();
        view.showBlankLine();
        cacheTraffic();
        view.showBlankLine();
        benchmark();
    }

    // ------------------------------------------------------------------
    // Parte 1: caché paso a paso
    // ------------------------------------------------------------------

    private void cacheStepByStep() {
        view.showSection("Parte 1: caché de capacidad 4 (clave = URL, valor = contenido)");
        LruCache<String, String> cache = new LruCache<>(4);
        for (String url : new String[] {"/inicio", "/login", "/perfil", "/ayuda"}) {
            cache.put(url, "contenido de " + url);
            view.showPut(url, cache.rootKey());
        }

        cache.get("/inicio");
        cache.get("/inicio");
        cache.get("/login");
        view.showRootAfterQueries(List.of("/inicio", "/inicio", "/login"), cache.rootKey());

        view.showBlankLine();
        view.showMessage("put(/tienda) con la caché llena -> se expulsa la entrada menos usada");
        cache.put("/tienda", "contenido de /tienda");
        view.showStillCached("/perfil", cache.get("/perfil") != null);
        view.showStats(cache.hits(), cache.misses(), cache.evictions(), cache.hitRate());
    }

    // ------------------------------------------------------------------
    // Parte 2: tráfico con la regla 80/20
    // ------------------------------------------------------------------

    private void cacheTraffic() {
        view.showSection("Parte 2: 2000 peticiones con regla 80/20 (capacidad 8)");
        LruCache<String, String> web = new LruCache<>(8);
        Lcg traffic = new Lcg(12345L);
        String[] hot = {"/inicio", "/login", "/panel", "/api/usuarios"};
        String[] cold = new String[16];
        for (int i = 0; i < cold.length; i++) {
            cold[i] = String.format("/blog/post-%02d", i + 1);
        }

        for (int i = 1; i <= 2000; i++) {
            String url = traffic.nextInt(100) < 80
                    ? hot[traffic.nextInt(hot.length)]
                    : cold[traffic.nextInt(cold.length)];
            if (web.get(url) == null) {                  // fallo: se "carga" y se guarda
                web.put(url, "contenido de " + url);
            }
            if (i == 10 || i == 100 || i == 2000) {
                view.showProgress(i, web.rootKey(), web.hits(), web.misses(),
                        web.evictions(), web.hitRate());
            }
        }
        view.showMessage("Primeros niveles del árbol (las páginas populares quedan arriba):");
        view.showLevels(web.levels(3));
    }

    // ------------------------------------------------------------------
    // Parte 3: benchmark Splay vs BST convencional
    // ------------------------------------------------------------------

    private void benchmark() {
        view.showSection("Parte 3: Splay vs BST convencional");
        view.showBenchmarkHeader(N, ACCESSES);
        Lcg lcg = new Lcg(12345L);
        int[] insertOrder = lcg.shuffledKeys(N);

        // Calentamiento de la JVM (silencioso). En Python no hace falta.
        int[] warm = new int[ACCESSES];
        for (int i = 0; i < ACCESSES; i++) {
            warm[i] = insertOrder[i % 100];
        }
        measure("calentamiento", insertOrder, warm);

        view.showBenchmarkTableHeader();

        // A) Un solo elemento repetido
        int[] a = new int[ACCESSES];
        for (int i = 0; i < ACCESSES; i++) {
            a[i] = insertOrder[N - 1];
        }
        view.showResult(measure("A) mismo elemento", insertOrder, a));

        // B) Conjunto caliente de HOT_SET claves, en rotación
        int[] b = new int[ACCESSES];
        for (int i = 0; i < ACCESSES; i++) {
            b[i] = insertOrder[(i % HOT_SET) * (N / HOT_SET)];
        }
        view.showResult(measure("B) conjunto caliente (10)", insertOrder, b));

        // C) Recorrido secuencial 0, 1, 2, ..., N-1
        int[] c = new int[N];
        for (int i = 0; i < N; i++) {
            c[i] = i;
        }
        view.showResult(measure("C) secuencial ordenado", insertOrder, c));

        // D) Accesos uniformes al azar
        int[] d = new int[ACCESSES];
        for (int i = 0; i < ACCESSES; i++) {
            d[i] = lcg.nextInt(N);
        }
        view.showResult(measure("D) uniforme al azar", insertOrder, d));

        view.showSeparator();
        view.showBenchmarkConclusion();
    }

    /** Reconstruye ambos árboles y mide solo los accesos de un escenario. */
    private ScenarioResult measure(String name, int[] insertOrder, int[] accessKeys) {
        SearchTree<Integer, Integer> bst = new PlainBst<>();
        SearchTree<Integer, Integer> splay = new SplayTree<>();
        for (int key : insertOrder) {
            bst.insert(key, key);
            splay.insert(key, key);
        }
        bst.resetComparisons();
        splay.resetComparisons();

        double bstMs = timeAccesses(bst, accessKeys);
        double splayMs = timeAccesses(splay, accessKeys);

        return new ScenarioResult(name, bstMs, splayMs,
                (double) bst.comparisons() / accessKeys.length,
                (double) splay.comparisons() / accessKeys.length);
    }

    private double timeAccesses(SearchTree<Integer, Integer> tree, int[] accessKeys) {
        long start = System.nanoTime();
        int found = 0;
        for (int key : accessKeys) {
            if (tree.search(key)) {
                found++;
            }
        }
        double ms = (System.nanoTime() - start) / 1e6;
        if (found != accessKeys.length) {
            throw new IllegalStateException("Alguna búsqueda falló: error en la implementación");
        }
        return ms;
    }
}
