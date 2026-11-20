# Árboles Splay

Dos ejemplos del Árbol Splay en **Java** y **Python**, con carpetas `model`, `view` y `controller`.

- **Básico:** insertar, buscar y eliminar, mostrando qué rotación se aplica (Zig, Zig-Zig, Zig-Zag).
- **Aplicado:** una caché y una comparación de velocidad contra un árbol normal.

## Estructura

```
java/src/                      python/
├── SplayTreeBasico.java       ├── splay_tree_basico.py
├── SplayTreeAplicado.java     ├── splay_tree_aplicado.py
├── model/                     ├── model/
├── view/                      ├── view/
└── controller/                └── controller/
```

## Cómo ejecutar

**Java** (desde la carpeta `java`):
```
javac -d out $(find src -name "*.java")
java -cp out SplayTreeBasico
java -cp out SplayTreeAplicado
```

**Python** (desde la carpeta `python`):
```
python splay_tree_basico.py
python splay_tree_aplicado.py
```