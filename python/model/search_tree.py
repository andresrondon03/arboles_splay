"""
Contrato común de los árboles de búsqueda que se comparan en el benchmark
(el Árbol Splay y el BST convencional).
"""

from abc import ABC, abstractmethod
from typing import Any


class SearchTree(ABC):

    @abstractmethod
    def insert(self, key: Any, value: Any = None) -> bool:
        """Inserta una clave. True si es nueva, False si ya existía (se actualiza su valor)."""

    @abstractmethod
    def search(self, key: Any) -> bool:
        """Busca una clave. True si existe."""

    @abstractmethod
    def comparisons(self) -> int:
        """Comparaciones de clave acumuladas durante las búsquedas."""

    @abstractmethod
    def reset_comparisons(self) -> None:
        """Reinicia el contador de comparaciones."""
