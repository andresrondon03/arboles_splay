"""
MODELO (datos de apoyo): generador pseudoaleatorio congruencial lineal.
Es idéntico en Java y Python, así que ambos lenguajes generan exactamente las mismas secuencias.
"""

from typing import List


class Lcg:

    def __init__(self, seed: int) -> None:
        self._seed = seed

    def next_int(self, bound: int) -> int:
        """Entero pseudoaleatorio en [0, bound)."""
        self._seed = (self._seed * 1103515245 + 12345) & 0x7FFFFFFF
        return (self._seed >> 8) % bound

    def shuffled_keys(self, n: int) -> List[int]:
        """Permutación de 0..n-1 (Fisher-Yates)."""
        keys = list(range(n))
        for i in range(n - 1, 0, -1):
            j = self.next_int(i + 1)
            keys[i], keys[j] = keys[j], keys[i]
        return keys
