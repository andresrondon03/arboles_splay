"""
CONTROLADOR del ejemplo aplicado: simula una caché y ejecuta un benchmark pequeño.
Decide los escenarios, mide, y entrega los datos a la vista.
"""

import time
from typing import List

from model.lcg import Lcg
from model.lru_cache import LruCache
from model.plain_bst import PlainBst
from model.scenario_result import ScenarioResult
from model.search_tree import SearchTree
from model.splay_tree import SplayTree
from view.applied_view import AppliedView


class AppliedController:
    N = 10_000            # claves en el árbol del benchmark
    ACCESSES = 100_000    # accesos por escenario
    HOT_SET = 10          # tamaño del conjunto caliente

    def __init__(self, view: AppliedView) -> None:
        self._view = view

    def run_demo(self) -> None:
        self._cache_step_by_step()
        self._view.show_blank_line()
        self._cache_traffic()
        self._view.show_blank_line()
        self._benchmark()

    # ------------------------------------------------------------------
    # Parte 1: caché paso a paso
    # ------------------------------------------------------------------

    def _cache_step_by_step(self) -> None:
        view = self._view
        view.show_section("Parte 1: caché de capacidad 4 (clave = URL, valor = contenido)")
        cache = LruCache(4)
        for url in ("/inicio", "/login", "/perfil", "/ayuda"):
            cache.put(url, "contenido de " + url)
            view.show_put(url, cache.root_key())

        cache.get("/inicio")
        cache.get("/inicio")
        cache.get("/login")
        view.show_root_after_queries(["/inicio", "/inicio", "/login"], cache.root_key())

        view.show_blank_line()
        view.show_message("put(/tienda) con la caché llena -> se expulsa la entrada menos usada")
        cache.put("/tienda", "contenido de /tienda")
        view.show_still_cached("/perfil", cache.get("/perfil") is not None)
        view.show_stats(cache.hits(), cache.misses(), cache.evictions(), cache.hit_rate())

    # ------------------------------------------------------------------
    # Parte 2: tráfico con la regla 80/20
    # ------------------------------------------------------------------

    def _cache_traffic(self) -> None:
        view = self._view
        view.show_section("Parte 2: 2000 peticiones con regla 80/20 (capacidad 8)")
        web = LruCache(8)
        traffic = Lcg(12345)
        hot = ["/inicio", "/login", "/panel", "/api/usuarios"]
        cold = [f"/blog/post-{i + 1:02d}" for i in range(16)]

        for i in range(1, 2001):
            if traffic.next_int(100) < 80:
                url = hot[traffic.next_int(len(hot))]
            else:
                url = cold[traffic.next_int(len(cold))]
            if web.get(url) is None:                     # fallo: se "carga" y se guarda
                web.put(url, "contenido de " + url)
            if i in (10, 100, 2000):
                view.show_progress(i, web.root_key(), web.hits(), web.misses(),
                                   web.evictions(), web.hit_rate())
        view.show_message("Primeros niveles del árbol (las páginas populares quedan arriba):")
        view.show_levels(web.levels(3))

    # ------------------------------------------------------------------
    # Parte 3: benchmark Splay vs BST convencional
    # ------------------------------------------------------------------

    def _benchmark(self) -> None:
        view = self._view
        n, accesses, hot_set = self.N, self.ACCESSES, self.HOT_SET
        view.show_section("Parte 3: Splay vs BST convencional")
        view.show_benchmark_header(n, accesses)
        lcg = Lcg(12345)
        insert_order = lcg.shuffled_keys(n)

        # (Java hace aquí un calentamiento de la JVM; en CPython no hace falta.)
        view.show_benchmark_table_header()

        # A) Un solo elemento repetido
        a = [insert_order[n - 1]] * accesses
        view.show_result(self._measure("A) mismo elemento", insert_order, a))

        # B) Conjunto caliente de HOT_SET claves, en rotación
        b = [insert_order[(i % hot_set) * (n // hot_set)] for i in range(accesses)]
        view.show_result(self._measure("B) conjunto caliente (10)", insert_order, b))

        # C) Recorrido secuencial 0, 1, 2, ..., N-1
        c = list(range(n))
        view.show_result(self._measure("C) secuencial ordenado", insert_order, c))

        # D) Accesos uniformes al azar
        d = [lcg.next_int(n) for _ in range(accesses)]
        view.show_result(self._measure("D) uniforme al azar", insert_order, d))

        view.show_separator()
        view.show_benchmark_conclusion()

    def _measure(self, name: str, insert_order: List[int],
                 access_keys: List[int]) -> ScenarioResult:
        """Reconstruye ambos árboles y mide solo los accesos de un escenario."""
        bst: SearchTree = PlainBst()
        splay: SearchTree = SplayTree()
        for key in insert_order:
            bst.insert(key, key)
            splay.insert(key, key)
        bst.reset_comparisons()
        splay.reset_comparisons()

        bst_ms = self._time_accesses(bst, access_keys)
        splay_ms = self._time_accesses(splay, access_keys)

        total = len(access_keys)
        return ScenarioResult(name, bst_ms, splay_ms,
                              bst.comparisons() / total, splay.comparisons() / total)

    @staticmethod
    def _time_accesses(tree: SearchTree, access_keys: List[int]) -> float:
        start = time.perf_counter()
        found = 0
        for key in access_keys:
            if tree.search(key):
                found += 1
        elapsed_ms = (time.perf_counter() - start) * 1000
        if found != len(access_keys):
            raise RuntimeError("Alguna búsqueda falló: error en la implementación")
        return elapsed_ms
