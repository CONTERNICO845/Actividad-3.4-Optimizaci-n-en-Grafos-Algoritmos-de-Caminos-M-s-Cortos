package gestiondegrafos;

import java.util.*;

public class AlgoritmoBellmanFord {

    private final GrafoPonderado grafo;

    public AlgoritmoBellmanFord(GrafoPonderado grafo) {
        this.grafo = grafo;
    }

    public ResultadoCamino ejecutar(String nombreOrigen, String nombreDestino) {
        ResultadoCamino resultado = new ResultadoCamino("Bellman-Ford");
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

        List<Vertice> vertices = grafo.getVertices();
        List<Arista> aristas = grafo.getAristas();
        int V = vertices.size();

        Map<Vertice, Double> dist = new HashMap<>();
        Map<Vertice, Vertice> previo = new HashMap<>();

        traza.append("╔══════════════════════════════════════════════════════════╗\n");
        traza.append("║       ALGORITMO DE BELLMAN-FORD — Camino Más Corto      ║\n");
        traza.append("╚══════════════════════════════════════════════════════════╝\n\n");
        traza.append("▶ FASE 1 — INICIALIZACIÓN\n");
        traza.append("─────────────────────────────────────────────────────────\n");
        traza.append("  Origen  : ").append(vOrigen.getNombre()).append("\n");
        traza.append("  Destino : ").append(vDestino.getNombre()).append("\n\n");
        traza.append("  Distancias iniciales asignadas a todos los vértices:\n");

        for (Vertice v : vertices) {
            if (v.equals(vOrigen)) {
                dist.put(v, 0.0);
                traza.append("    dist[").append(v.getNombre()).append("] = 0  ← nodo origen\n");
            } else {
                dist.put(v, Double.POSITIVE_INFINITY);
                traza.append("    dist[").append(v.getNombre()).append("] = ∞\n");
            }
            previo.put(v, null);
        }

        traza.append("\n  Número de vértices (V) = ").append(V).append("\n");
        traza.append("  Iteraciones a realizar = V - 1 = ").append(V - 1).append("\n");
        traza.append("  Aristas totales        = ").append(aristas.size()).append("\n\n");

        traza.append("▶ FASE 2 — RELAJACIÓN DE ARISTAS (").append(V - 1).append(" iteraciones)\n");
        traza.append("─────────────────────────────────────────────────────────\n");

        int totalEvaluaciones = 0;
        int totalDescartes = 0;
        int totalActualizaciones = 0;

        for (int i = 1; i <= V - 1; i++) {
            traza.append("\n  ┌─ Iteración #").append(i).append(" de ").append(V - 1)
                 .append(" ─────────────────────────────\n");
            boolean huboCambio = false;

            for (Arista arista : aristas) {
                Vertice u = arista.getOrigen();
                Vertice v = arista.getDestino();
                double peso = arista.getPeso();
                totalEvaluaciones++;

                if (dist.get(u) == Double.POSITIVE_INFINITY) {
                    traza.append("  │   Arista ").append(u.getNombre()).append(" ──[").append(peso)
                         .append("]──▶ ").append(v.getNombre())
                         .append("  →  dist[").append(u.getNombre()).append("] = ∞, no se puede relajar. OMITIDA.\n");
                    totalDescartes++;
                    continue;
                }

                double nuevaDist = dist.get(u) + peso;
                double distActualV = dist.get(v);

                traza.append("  │   Evaluando: ").append(u.getNombre())
                     .append(" ──[").append(peso).append("]──▶ ").append(v.getNombre()).append("\n");
                traza.append("  │     dist[").append(u.getNombre()).append("] + peso = ")
                     .append(dist.get(u)).append(" + ").append(peso).append(" = ").append(nuevaDist).append("\n");
                traza.append("  │     dist[").append(v.getNombre()).append("] actual = ")
                     .append(distActualV == Double.POSITIVE_INFINITY ? "∞" : distActualV).append("\n");

                if (nuevaDist < distActualV) {
                    dist.put(v, nuevaDist);
                    previo.put(v, u);
                    huboCambio = true;
                    totalActualizaciones++;
                    traza.append("  │     ✔ RELAJACIÓN ACEPTADA → dist[").append(v.getNombre())
                         .append("] actualizado a ").append(nuevaDist).append("\n");
                    traza.append("  │     Previo[").append(v.getNombre()).append("] = ").append(u.getNombre()).append("\n");
                } else {
                    totalDescartes++;
                    traza.append("  │     ✘ DESCARTADO → ").append(nuevaDist)
                         .append(" ≥ dist actual ")
                         .append(distActualV == Double.POSITIVE_INFINITY ? "∞" : distActualV)
                         .append(" (no mejora)\n");
                }
            }

            traza.append("  └────────────────────────────────────────────────\n");
            traza.append("    Estado de distancias tras iteración #").append(i).append(":\n");
            for (Vertice v : vertices) {
                double d = dist.get(v);
                traza.append("      dist[").append(v.getNombre()).append("] = ")
                     .append(d == Double.POSITIVE_INFINITY ? "∞" : d).append("\n");
            }

            if (!huboCambio) {
                traza.append("\n  ✅ Sin cambios en iteración #").append(i)
                     .append(" — El algoritmo convergió anticipadamente.\n");
                break;
            }
        }

        traza.append("\n▶ FASE 3 — DETECCIÓN DE CICLOS NEGATIVOS\n");
        traza.append("─────────────────────────────────────────────────────────\n");
        traza.append("  Realizando una iteración adicional (V-ésima) para detectar ciclos:\n\n");

        boolean cicloNegativo = false;
        for (Arista arista : aristas) {
            Vertice u = arista.getOrigen();
            Vertice v = arista.getDestino();
            double peso = arista.getPeso();

            if (dist.get(u) == Double.POSITIVE_INFINITY) continue;

            double nuevaDist = dist.get(u) + peso;
            if (nuevaDist < dist.get(v)) {
                cicloNegativo = true;
                traza.append("  ⚠️  CICLO NEGATIVO DETECTADO en arista: ")
                     .append(u.getNombre()).append(" ──[").append(peso).append("]──▶ ")
                     .append(v.getNombre()).append("\n");
                traza.append("     dist[").append(u.getNombre()).append("] + peso = ")
                     .append(nuevaDist).append(" < dist[").append(v.getNombre()).append("] = ")
                     .append(dist.get(v)).append("\n");
            }
        }

        if (!cicloNegativo) {
            traza.append("  ✅ No se detectaron ciclos negativos en el grafo.\n");
        } else {
            traza.append("\n  ❌ El grafo contiene ciclos negativos.\n");
            traza.append("     Los caminos mínimos no están bien definidos.\n");
            resultado.setCicloNegativo(true);
            resultado.setAlcanzable(false);
            resultado.setTraza(traza.toString());
            resultado.setTotalEvaluaciones(totalEvaluaciones);
            resultado.setTotalDescartes(totalDescartes);
            resultado.setTotalActualizaciones(totalActualizaciones);
            resultado.setTiempoMs(System.currentTimeMillis() - inicio);
            return resultado;
        }

        traza.append("\n▶ FASE 4 — RECONSTRUCCIÓN DEL CAMINO ÓPTIMO\n");
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
            Set<Vertice> visitados = new HashSet<>();
            while (cursor != null && !visitados.contains(cursor)) {
                camino.addFirst(cursor);
                visitados.add(cursor);
                cursor = previo.get(cursor);
            }

            traza.append("  Reconstruyendo desde destino hacia origen usando mapa 'Previo':\n");
            for (int i = camino.size() - 1; i >= 0; i--) {
                Vertice prev = previo.get(camino.get(i));
                if (prev != null) {
                    traza.append("    ").append(camino.get(i).getNombre())
                         .append(" ←── previo[").append(camino.get(i).getNombre()).append("] = ")
                         .append(prev.getNombre()).append("\n");
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
