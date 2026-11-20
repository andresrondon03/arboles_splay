"""
MODELO: caché de capacidad fija respaldada por el Árbol Splay (SplayTree).

Cada get()/put() sube la entrada a la raíz, así que las más consultadas quedan arriba.
Política de expulsión LRU: cada entrada guarda un "reloj lógico" con su último acceso y,
al llenarse, se elimina la más antigua.

NOTA DIDÁCTICA: encontrar la entrada más antigua recorre todo el árbol (O(n)); es aceptable
para una caché pequeña de demostración. Una caché LRU de producción combina un diccionario
con una lista doblemente enlazada para lograr O(1).
"""

from typing import Any, List, Optional

from model.splay_tree import SplayTree


class _Entry:
    """Valor guardado en el árbol: el dato más su último acceso."""

    __slots__ = ("value", "last_access")

    def __init__(self, value: Any, last_access: int) -> None:
        self.value = value
        self.last_access = last_access


class LruCache:

    def __init__(self, capacity: int) -> None:
        if capacity < 1:
            raise ValueError("La capacidad debe ser al menos 1")
        self._tree = SplayTree()
        self._capacity = capacity
        self._clock = 0
        self._hits = 0
        self._misses = 0
        self._evictions = 0

    def get(self, key: Any) -> Optional[Any]:
        """Acierto (hit): sube la entrada a la raíz y devuelve el valor. Fallo (miss): None."""
        entry = self._tree.get(key)
        if entry is None:
            self._misses += 1
            return None
        self._hits += 1
        self._clock += 1
        entry.last_access = self._clock
        return entry.value

    def put(self, key: Any, value: Any) -> None:
        """Inserta o actualiza una entrada; si se excede la capacidad, expulsa la menos usada."""
        self._clock += 1
        added = self._tree.insert(key, _Entry(value, self._clock))
        if added and self._tree.size() > self._capacity:
            self._evict_least_recently_used()

    def _evict_least_recently_used(self) -> None:
        victim_key = None
        oldest = None
        for key, entry in self._tree.entries():
            if oldest is None or entry.last_access < oldest:
                oldest = entry.last_access
                victim_key = key
        self._tree.delete(victim_key)
        self._evictions += 1

    # ---- Consultas de estado (solo datos, sin formato) ----

    def size(self) -> int:
        return self._tree.size()

    def root_key(self) -> Optional[Any]:
        return self._tree.root_key()

    def hits(self) -> int:
        return self._hits

    def misses(self) -> int:
        return self._misses

    def evictions(self) -> int:
        return self._evictions

    def hit_rate(self) -> float:
        total = self._hits + self._misses
        return 0.0 if total == 0 else self._hits / total

    def levels(self, max_levels: int) -> List[List[Any]]:
        """Claves por nivel (recorrido por niveles): la vista decide cómo mostrarlas."""
        result: List[List[Any]] = []
        root = self._tree.get_root()
        level = [root] if root is not None else []
        depth = 0
        while depth < max_levels and level:
            result.append([node.key for node in level])
            nxt = []
            for node in level:
                if node.left is not None:
                    nxt.append(node.left)
                if node.right is not None:
                    nxt.append(node.right)
            level = nxt
            depth += 1
        return result
