package gestiondegrafos;

import java.util.ArrayList;
import java.util.List;

public class GrafoPonderado {

    private List<Vertice> vertices;
    private List<Arista> aristas;
    private boolean dirigido;

    public GrafoPonderado(boolean dirigido) {
        this.vertices = new ArrayList<>();
        this.aristas = new ArrayList<>();
        this.dirigido = dirigido;
    }

    public boolean agregarVertice(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (buscarVertice(nombre) != null) return false;
        vertices.add(new Vertice(nombre.trim()));
        return true;
    }

    public boolean eliminarVertice(String nombre) {
        Vertice v = buscarVertice(nombre);
        if (v == null) return false;
        aristas.removeIf(a -> a.getOrigen().equals(v) || a.getDestino().equals(v));
        vertices.remove(v);
        return true;
    }

    public Vertice buscarVertice(String nombre) {
        for (Vertice v : vertices) {
            if (v.getNombre().equalsIgnoreCase(nombre.trim())) return v;
        }
        return null;
    }

    public List<Vertice> getVertices() {
        return new ArrayList<>(vertices);
    }

    public boolean agregarArista(String nombreOrigen, String nombreDestino, double peso) {
        Vertice origen = buscarVertice(nombreOrigen);
        Vertice destino = buscarVertice(nombreDestino);
        if (origen == null || destino == null) return false;
        if (origen.equals(destino)) return false;

        if (buscarArista(nombreOrigen, nombreDestino) != null) return false;

        aristas.add(new Arista(origen, destino, peso));
        if (!dirigido) {
            aristas.add(new Arista(destino, origen, peso));
        }
        return true;
    }

    public boolean eliminarArista(String nombreOrigen, String nombreDestino) {
        Arista a = buscarArista(nombreOrigen, nombreDestino);
        if (a == null) return false;
        aristas.remove(a);
        if (!dirigido) {
            Arista inv = buscarArista(nombreDestino, nombreOrigen);
            if (inv != null) aristas.remove(inv);
        }
        return true;
    }

    public Arista buscarArista(String nombreOrigen, String nombreDestino) {
        for (Arista a : aristas) {
            if (a.getOrigen().getNombre().equalsIgnoreCase(nombreOrigen.trim())
                    && a.getDestino().getNombre().equalsIgnoreCase(nombreDestino.trim())) {
                return a;
            }
        }
        return null;
    }

    public List<Arista> getAristas() {
        return new ArrayList<>(aristas);
    }

    public List<Arista> getAdyacentes(Vertice v) {
        List<Arista> adj = new ArrayList<>();
        for (Arista a : aristas) {
            if (a.getOrigen().equals(v)) adj.add(a);
        }
        return adj;
    }

    public boolean esDirigido() {
        return dirigido;
    }

    public void setDirigido(boolean dirigido) {
        this.dirigido = dirigido;
    }

    public void limpiar() {
        vertices.clear();
        aristas.clear();
    }

    public boolean tienePesosNegativos() {
        for (Arista a : aristas) {
            if (a.getPeso() < 0) return true;
        }
        return false;
    }

    public int getIndice(Vertice v) {
        return vertices.indexOf(v);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo ").append(dirigido ? "Dirigido" : "No Dirigido")
          .append(" | Vértices: ").append(vertices.size())
          .append(" | Aristas: ").append(dirigido ? aristas.size() : aristas.size() / 2)
          .append("\n");
        for (Arista a : aristas) {
            sb.append("  ").append(a).append("\n");
        }
        return sb.toString();
    }
}
