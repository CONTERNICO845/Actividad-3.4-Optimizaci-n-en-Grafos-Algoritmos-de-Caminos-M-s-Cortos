# Gestión y Optimización de Grafos Ponderados
## Actividad 3.3 / 3.4 — Estructuras de Datos

---

## Equipo
- Carmona Barbosa Geovani Gael
- Espinoza López Manuel Ángel
- Leyva Perez Victor Manuel

---

## Descripción
Aplicación Java Swing para la gestión visual de grafos ponderados y la optimización de rutas mediante **dos algoritmos de caminos más cortos**:

- **Algoritmo de Dijkstra** — Ruta más corta con pesos no negativos
- **Algoritmo de Bellman-Ford** — Ruta más corta con soporte para pesos negativos y detección de ciclos

---

## Estructura del Proyecto

```
GestiondeGrafos/
├── src/
│   └── gestiondegrafos/
│       ├── Vertice.java            → Modelo: nodo del grafo
│       ├── Arista.java             → Modelo: conexión ponderada
│       ├── GrafoPonderado.java     → Estructura del grafo (Act. 3.3)
│       ├── ResultadoCamino.java    → Encapsula resultado de algoritmos
│       ├── AlgoritmoDijkstra.java  → Implementación Dijkstra (Act. 3.4)
│       ├── AlgoritmoBellmanFord.java → Implementación Bellman-Ford (Act. 3.4)
│       ├── PanelGrafo.java         → Componente gráfico de visualización
│       └── VentanaPrincipal.java   → Ventana principal Swing
├── build/classes/                  → Clases compiladas
├── dist/GestionGrafos.jar          → JAR ejecutable
├── compilar.bat                    → Script de compilación
├── ejecutar.bat                    → Script de ejecución
└── build.xml                       → Build Ant (NetBeans)
```

---

## Cómo Compilar y Ejecutar

### Opción 1 — Script rápido
```
compilar.bat
ejecutar.bat
```

### Opción 2 — Línea de comandos
```bash
javac -encoding UTF-8 -d build/classes -sourcepath src src/gestiondegrafos/*.java
jar cfm dist/GestionGrafos.jar MANIFEST.MF -C build/classes .
java -jar dist/GestionGrafos.jar
```

### Opción 3 — NetBeans
Abrir la carpeta como proyecto Java existente en NetBeans y ejecutar con F6.

---

## Funcionalidades

### Gestión del Grafo (Act. 3.3)
- Agregar / eliminar vértices
- Agregar / eliminar aristas con peso
- Soporte para grafos dirigidos y no dirigidos
- Visualización gráfica en tiempo real con resaltado del camino

### Optimización de Rutas (Act. 3.4)
- Seleccionar nodo origen y destino via ComboBox
- Ejecutar **Dijkstra** o **Bellman-Ford** individualmente
- Ejecución **comparativa** de ambos algoritmos en un solo clic
- JTextArea de traza con desglose paso a paso:
  - Fase de inicialización (distancias ∞ y origen = 0)
  - Proceso de relajación de aristas (aceptadas y descartadas)
  - Reconstrucción del camino óptimo
  - Estadísticas de desempeño
- Comparativa de métricas: costo, evaluaciones, descartes, tiempo
- Resaltado visual del camino óptimo en el panel gráfico
- Validaciones completas (pesos negativos, nodos inexistentes, sin ruta)

---

## Algoritmos Implementados

### Dijkstra
- Complejidad: O((V + E) log V) con cola de prioridad
- Requiere pesos no negativos
- Detención anticipada al alcanzar el destino

### Bellman-Ford
- Complejidad: O(V × E)
- Soporta pesos negativos
- Detecta ciclos negativos (V-ésima iteración)
- Optimización: convergencia anticipada sin cambios

---

## Pruebas de Funcionamiento

El grafo de demostración precargado (7 nodos A-G) permite verificar:
1. **Nodos vecinos directos**: A → B (costo 4)
2. **Nodos distantes**: A → G atravesando múltiples vértices
3. **Traza detallada**: Aceptaciones y descartes visibles en JTextArea
4. **Sin ruta**: Eliminar aristas para dejar nodos aislados
5. **Comparativa**: Ambos algoritmos deben coincidir en costo óptimo
