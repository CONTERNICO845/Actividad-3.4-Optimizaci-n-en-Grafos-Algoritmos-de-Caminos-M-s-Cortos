package gestiondegrafos;

import java.util.*;

public class AlgoritmoDijkstra {

    private final GrafoPonderado grafo;

    public AlgoritmoDijkstra(GrafoPonderado grafo) {
        this.grafo = grafo;
    }

    public ResultadoCamino ejecutar(String nombreOrigen, String nombreDestino) {
        ResultadoCamino resultado = new ResultadoCamino("Dijkstra");
        long inicio = System.currentTimeMillis();
        StringBuilder traza = new StringBuilder();

        Vertice vOrigen = grafo.buscarVertice(nombreOrigen);
        Vertice vDestino = grafo.buscarVertice(nombreDestino);

        if (vOrigen == null) {
            traza.append("❌ ERROR: El nodo origen '").append(nombreOrigen).append("' no existe en el grafo.\n");
            resultado.setTraza(traza.toString());
            return resultado;
        }
        if (vDestino == null) {
            traza.append("❌ ERROR: El nodo destino '").append(nombreDestino).append("' no existe en el grafo.\n");
            resultado.setTraza(traza.toString());
            return resultado;
        }
        if (grafo.tienePesosNegativos()) {
            traza.append("❌ ERROR: El grafo contiene pesos negativos.\n");
            traza.append("   Dijkstra no garantiza resultados correctos con pesos negativos.\n");
            traza.append("   Use el algoritmo de Bellman-Ford para este caso.\n");
            resultado.setTraza(traza.toString());
            return resultado;
        }

        List<Vertice> vertices = grafo.getVertices();
        int n = vertices.size();

        Map<Vertice, Double> dist = new HashMap<>();
        Map<Vertice, Vertice> previo = new HashMap<>();
        Set<Vertice> visitados = new HashSet<>();

        PriorityQueue<double[]> colaPQ = new PriorityQueue<>(Comparator.comparingDouble(e -> e[0]));
        Map<Integer, Vertice> indiceVertice = new HashMap<>();

        traza.append("╔══════════════════════════════════════════════════════════╗\n");
        traza.append("║          ALGORITMO DE DIJKSTRA — Camino Más Corto       ║\n");
        traza.append("╚══════════════════════════════════════════════════════════╝\n\n");
        traza.append("▶ FASE 1 — INICIALIZACIÓN\n");
        traza.append("─────────────────────────────────────────────────────────\n");
        traza.append("  Origen  : ").append(vOrigen.getNombre()).append("\n");
        traza.append("  Destino : ").append(vDestino.getNombre()).append("\n\n");
        traza.append("  Distancias iniciales asignadas a todos los vértices:\n");

        for (int i = 0; i < n; i++) {
            Vertice v = vertices.get(i);
            indiceVertice.put(i, v);
            if (v.equals(vOrigen)) {
                dist.put(v, 0.0);
                traza.append("    dist[").append(v.getNombre()).append("] = 0  ← nodo origen\n");
                colaPQ.offer(new double[]{0.0, i});
            } else {
                dist.put(v, Double.POSITIVE_INFINITY);
                traza.append("    dist[").append(v.getNombre()).append("] = ∞\n");
            }
            previo.put(v, null);
        }
        traza.append("\n  Cola de prioridad inicializada con: (0, ").append(vOrigen.getNombre()).append(")\n\n");

        traza.append("▶ FASE 2 — PROCESAMIENTO (Relajación de Aristas)\n");
        traza.append("─────────────────────────────────────────────────────────\n");

        int totalEvaluaciones = 0;
        int totalDescartes = 0;
        int totalActualizaciones = 0;
        int iteracion = 0;

        while (!colaPQ.isEmpty()) {
            double[] actual = colaPQ.poll();
            double distActual = actual[0];
            Vertice u = indiceVertice.get((int) actual[1]);

            if (visitados.contains(u)) {
                totalDescartes++;
                traza.append("  [DESCARTAR] Nodo '").append(u.getNombre())
                     .append("' ya fue procesado con menor costo. Se omite.\n");
                continue;
            }

            visitados.add(u);
            iteracion++;
            traza.append("\n  ┌─ Iteración #").append(iteracion).append(" ─────────────────────────────\n");
            traza.append("  │ Extrayendo nodo de menor costo: '").append(u.getNombre())
                 .append("'  (dist acumulada = ").append(distActual).append(")\n");

            if (u.equals(vDestino)) {
                traza.append("  │ ✅ DESTINO ALCANZADO: '").append(vDestino.getNombre()).append("'\n");
                traza.append("  └────────────────────────────────────────────────\n\n");
                break;
            }

            List<Arista> adyacentes = grafo.getAdyacentes(u);
            if (adyacentes.isEmpty()) {
                traza.append("  │ Sin vecinos que explorar desde '").append(u.getNombre()).append("'.\n");
            }

            for (Arista arista : adyacentes) {
                Vertice v = arista.getDestino();
                double peso = arista.getPeso();
                totalEvaluaciones++;

                double nuevaDist = dist.get(u) + peso;
                double distActualV = dist.get(v);

                traza.append("  │   Evaluando arista: ").append(u.getNombre())
                     .append(" ──[").append(peso).append("]──▶ ").append(v.getNombre()).append("\n");
                traza.append("  │     dist[").append(u.getNombre()).append("] + peso = ")
                     .append(dist.get(u)).append(" + ").append(peso).append(" = ").append(nuevaDist).append("\n");
                traza.append("  │     dist[").append(v.getNombre()).append("] actual = ")
                     .append(distActualV == Double.POSITIVE_INFINITY ? "∞" : distActualV).append("\n");

                if (nuevaDist < distActualV) {
                    dist.put(v, nuevaDist);
                    previo.put(v, u);
                    colaPQ.offer(new double[]{nuevaDist, vertices.indexOf(v)});
                    totalActualizaciones++;
                    traza.append("  │     ✔ RELAJACIÓN ACEPTADA → dist[").append(v.getNombre())
                         .append("] actualizado a ").append(nuevaDist).append("\n");
                    traza.append("  │     Previo[").append(v.getNombre()).append("] = ").append(u.getNombre()).append("\n");
                } else {
                    totalDescartes++;
                    traza.append("  │     ✘ DESCARTADO → ").append(nuevaDist)
                         .append(" ≥ dist actual ").append(distActualV == Double.POSITIVE_INFINITY ? "∞" : distActualV)
                         .append(" (no mejora)\n");
                }
            }
            traza.append("  └────────────────────────────────────────────────\n");
        }

        traza.append("\n▶ FASE 3 — RECONSTRUCCIÓN DEL CAMINO ÓPTIMO\n");
        traza.append("─────────────────────────────────────────────────────────\n");

        double costoFinal = dist.get(vDestino);

        if (costoFinal == Double.POSITIVE_INFINITY) {
            traza.append("  ❌ No existe camino entre '").append(vOrigen.getNombre())
                 .append("' y '").append(vDestino.getNombre()).append("'.\n");
            traza.append("  Los nodos no están conectados en el grafo.\n");
            resultado.setAlcanzable(false);
        } else {
            LinkedList<Vertice> camino = new LinkedList<>();
            Vertice cursor = vDestino;
            while (cursor != null) {
                camino.addFirst(cursor);
                cursor = previo.get(cursor);
            }

            traza.append("  Reconstruyendo desde destino hacia origen usando mapa 'Previo':\n");
            for (int i = camino.size() - 1; i >= 0; i--) {
                if (i > 0) {
                    traza.append("    ").append(camino.get(i).getNombre())
                         .append(" ←── previo[").append(camino.get(i).getNombre()).append("] = ")
                         .append(previo.get(camino.get(i)) != null ? previo.get(camino.get(i)).getNombre() : "—").append("\n");
                }
            }

            traza.append("\n  ╔══════════════════════════════════════════════════╗\n");
            traza.append("  ║  RUTA ÓPTIMA ENCONTRADA:                         ║\n");
            traza.append("  ║  ");
            StringBuilder rutaStr = new StringBuilder();
            for (int i = 0; i < camino.size(); i++) {
                rutaStr.append(camino.get(i).getNombre());
                if (i < camino.size() - 1) rutaStr.append(" → ");
            }
            traza.append(String.format("%-46s", rutaStr.toString())).append("  ║\n");
            traza.append("  ║  Costo total acumulado: ").append(String.format("%-26s", costoFinal)).append("  ║\n");
            traza.append("  ╚══════════════════════════════════════════════════╝\n");

            resultado.setRuta(new ArrayList<>(camino));
            resultado.setCostoTotal(costoFinal);
            resultado.setAlcanzable(true);
        }

        long tiempoTotal = System.currentTimeMillis() - inicio;
        resultado.setTotalEvaluaciones(totalEvaluaciones);
        resultado.setTotalDescartes(totalDescartes);
        resultado.setTotalActualizaciones(totalActualizaciones);
        resultado.setTiempoMs(tiempoTotal);

        traza.append("\n▶ ESTADÍSTICAS DE EJECUCIÓN\n");
        traza.append("─────────────────────────────────────────────────────────\n");
        traza.append("  Total de aristas evaluadas  : ").append(totalEvaluaciones).append("\n");
        traza.append("  Relajaciones aceptadas      : ").append(totalActualizaciones).append("\n");
        traza.append("  Alternativas descartadas    : ").append(totalDescartes).append("\n");
        traza.append("  Tiempo de ejecución         : ").append(tiempoTotal).append(" ms\n");

        resultado.setTraza(traza.toString());
        return resultado;
    }
}
