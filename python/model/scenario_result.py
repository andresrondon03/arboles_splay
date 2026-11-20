"""MODELO (objeto de datos inmutable): resultado de un escenario del benchmark."""

from dataclasses import dataclass


@dataclass(frozen=True)
class ScenarioResult:
    name: str
    bst_ms: float
    splay_ms: float
    bst_avg_comparisons: float
    splay_avg_comparisons: float

    def speedup(self) -> float:
        return self.bst_ms / self.splay_ms
