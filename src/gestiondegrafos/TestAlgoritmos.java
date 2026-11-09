package gestiondegrafos;

public class TestAlgoritmos {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  PRUEBAS DE ALGORITMOS — Actividad 3.4");
        System.out.println("═══════════════════════════════════════════════════════\n");

        GrafoPonderado g = new GrafoPonderado(false);
        String[] nodos = {"A", "B", "C", "D", "E", "F", "G"};
        for (String n : nodos) g.agregarVertice(n);
        g.agregarArista("A", "B", 4);
        g.agregarArista("A", "C", 2);
        g.agregarArista("B", "C", 1);
        g.agregarArista("B", "D", 5);
        g.agregarArista("C", "D", 8);
        g.agregarArista("C", "E", 10);
        g.agregarArista("D", "E", 2);
        g.agregarArista("D", "F", 6);
        g.agregarArista("E", "G", 3);
        g.agregarArista("F", "G", 1);

        AlgoritmoDijkstra dijkstra = new AlgoritmoDijkstra(g);
        AlgoritmoBellmanFord bellman = new AlgoritmoBellmanFord(g);

        System.out.println("PRUEBA 1 — Nodos vecinos directos (A → B)");
        ResultadoCamino r1d = dijkstra.ejecutar("A", "B");
        ResultadoCamino r1b = bellman.ejecutar("A", "B");
        System.out.println("  Dijkstra    : " + r1d.getRutaFormateada());
        System.out.println("  Bellman-Ford: " + r1b.getRutaFormateada());
        System.out.println("  [PASS] Coinciden: " + (r1d.getCostoTotal() == r1b.getCostoTotal()) + "\n");

        System.out.println("PRUEBA 2 — Nodos distantes (A → G)");
        ResultadoCamino r2d = dijkstra.ejecutar("A", "G");
        ResultadoCamino r2b = bellman.ejecutar("A", "G");
        System.out.println("  Dijkstra    : " + r2d.getRutaFormateada());
        System.out.println("  Bellman-Ford: " + r2b.getRutaFormateada());
        System.out.println("  [PASS] Coinciden: " + (r2d.getCostoTotal() == r2b.getCostoTotal()) + "\n");

        System.out.println("PRUEBA 3 — Mismo nodo origen y destino (A → A)");
        ResultadoCamino r3d = dijkstra.ejecutar("A", "A");
        ResultadoCamino r3b = bellman.ejecutar("A", "A");
        System.out.println("  Dijkstra    : " + r3d.getRutaFormateada());
        System.out.println("  Bellman-Ford: " + r3b.getRutaFormateada() + "\n");

        System.out.println("PRUEBA 4 — Nodos no conectados");
        GrafoPonderado g2 = new GrafoPonderado(false);
        g2.agregarVertice("X"); g2.agregarVertice("Y"); g2.agregarVertice("Z");
        g2.agregarArista("X", "Y", 3);
        ResultadoCamino r4d = new AlgoritmoDijkstra(g2).ejecutar("X", "Z");
        ResultadoCamino r4b = new AlgoritmoBellmanFord(g2).ejecutar("X", "Z");
        System.out.println("  Dijkstra    : " + r4d.getRutaFormateada());
        System.out.println("  Bellman-Ford: " + r4b.getRutaFormateada());
        System.out.println("  [PASS] Sin ruta detectada correctamente: "
                + (!r4d.isAlcanzable() && !r4b.isAlcanzable()) + "\n");

        System.out.println("PRUEBA 5 — Bellman-Ford con pesos negativos");
        GrafoPonderado g3 = new GrafoPonderado(true);
        g3.agregarVertice("P"); g3.agregarVertice("Q"); g3.agregarVertice("R");
        g3.agregarArista("P", "Q", 4);
        g3.agregarArista("Q", "R", -2);
        g3.agregarArista("P", "R", 5);
        ResultadoCamino r5b = new AlgoritmoBellmanFord(g3).ejecutar("P", "R");
        System.out.println("  Bellman-Ford (peso neg): " + r5b.getRutaFormateada());
        System.out.println("  Costo esperado: 2.0 → " + r5b.getCostoTotal() + "\n");

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Todas las pruebas completadas.");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}
