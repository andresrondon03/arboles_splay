"""VISTA del ejemplo aplicado: caché y benchmark. Solo formatea datos ya calculados."""

from typing import List

from model.scenario_result import ScenarioResult


class AppliedView:
    WIDTH = 86

    # ---- General ----

    def show_section(self, title: str) -> None:
        print(f"=== {title} ===")

    def show_blank_line(self) -> None:
        print()

    def show_message(self, text: str) -> None:
        print(text)

    # ---- Caché ----

    def show_put(self, key: str, root_key: str) -> None:
        print(f"put({key:<8}) -> raíz = {root_key}")

    def show_root_after_queries(self, queried: List[str], root_key: str) -> None:
        print(f"Tras consultar {', '.join(queried)} -> raíz = {root_key}")

    def show_still_cached(self, key: str, cached: bool) -> None:
        print(f"¿Sigue {key} en caché? " + ("sí" if cached else "no (fue expulsada)"))

    def show_stats(self, hits: int, misses: int, evictions: int, hit_rate: float) -> None:
        print("Estadísticas: " + self._format_stats(hits, misses, evictions, hit_rate))

    def show_progress(self, requests: int, root_key: str, hits: int, misses: int,
                      evictions: int, hit_rate: float) -> None:
        print(f"Tras {requests:4d} peticiones -> raíz = {root_key:<14} | "
              + self._format_stats(hits, misses, evictions, hit_rate))

    def show_levels(self, levels: List[List[str]]) -> None:
        for depth, keys in enumerate(levels):
            print(f"  Nivel {depth}: [{', '.join(str(k) for k in keys)}]")

    @staticmethod
    def _format_stats(hits: int, misses: int, evictions: int, hit_rate: float) -> str:
        return (f"aciertos={hits} | fallos={misses} | "
                f"expulsiones={evictions} | tasa de acierto={hit_rate * 100:.1f}%")

    # ---- Benchmark ----

    def show_benchmark_header(self, n: int, accesses: int) -> None:
        print(f"Árbol con N = {n} claves | {accesses} accesos por escenario")
        print("(comp = comparaciones promedio por acceso)\n")

    def show_benchmark_table_header(self) -> None:
        print(f"{'Escenario':<26} | {'BST (ms)':>9} | {'Splay(ms)':>9} | "
              f"{'BST comp':>8} | {'Spl comp':>8} | {'BST/Spl':>7}")
        self.show_separator()

    def show_separator(self) -> None:
        print("-" * self.WIDTH)

    def show_result(self, r: ScenarioResult) -> None:
        print(f"{r.name:<26} | {r.bst_ms:9.1f} | {r.splay_ms:9.1f} | "
              f"{r.bst_avg_comparisons:8.2f} | {r.splay_avg_comparisons:8.2f} | {r.speedup():6.2f}x")

    def show_benchmark_conclusion(self) -> None:
        print("Conclusión: medido en comparaciones, el Splay gana con localidad de")
        print("referencia (A, B, C) y no gana en D, donde los accesos no tienen patrón.")
        print("El tiempo depende del lenguaje y su entorno: el costo de las rotaciones pesa.")
