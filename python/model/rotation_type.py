"""
Tipos de paso que puede ejecutar la rutina splay.
El modelo los REGISTRA (no imprime nada); la vista decide cómo mostrarlos.
"""

from enum import Enum


class RotationType(Enum):
    ZIG = "ZIG"              # el padre es la raíz: una sola rotación simple
    ZIG_ZIG = "ZIG-ZIG"      # x y su padre del mismo lado: primero rota el padre, luego x
    ZIG_ZAG = "ZIG-ZAG"      # x y su padre de lados opuestos: x rota dos veces

    def label(self) -> str:
        return self.value
