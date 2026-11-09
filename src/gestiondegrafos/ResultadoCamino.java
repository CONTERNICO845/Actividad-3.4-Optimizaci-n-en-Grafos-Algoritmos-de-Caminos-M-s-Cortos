package gestiondegrafos;

import java.util.List;

public class ResultadoCamino {

    private List<Vertice> ruta;
    private double costoTotal;
    private boolean alcanzable;
    private boolean cicloNegativo;
    private String traza;
    private int totalEvaluaciones;
    private int totalDescartes;
    private int totalActualizaciones;
    private long tiempoMs;
    private String nombreAlgoritmo;

    public ResultadoCamino(String nombreAlgoritmo) {
        this.nombreAlgoritmo = nombreAlgoritmo;
        this.alcanzable = false;
        this.cicloNegativo = false;
        this.costoTotal = Double.POSITIVE_INFINITY;
        this.totalEvaluaciones = 0;
        this.totalDescartes = 0;
        this.totalActualizaciones = 0;
    }

    public List<Vertice> getRuta() { return ruta; }
    public void setRuta(List<Vertice> ruta) { this.ruta = ruta; }

    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    public boolean isAlcanzable() { return alcanzable; }
    public void setAlcanzable(boolean alcanzable) { this.alcanzable = alcanzable; }

    public boolean isCicloNegativo() { return cicloNegativo; }
    public void setCicloNegativo(boolean cicloNegativo) { this.cicloNegativo = cicloNegativo; }

    public String getTraza() { return traza; }
    public void setTraza(String traza) { this.traza = traza; }

    public int getTotalEvaluaciones() { return totalEvaluaciones; }
    public void setTotalEvaluaciones(int totalEvaluaciones) { this.totalEvaluaciones = totalEvaluaciones; }

    public int getTotalDescartes() { return totalDescartes; }
    public void setTotalDescartes(int totalDescartes) { this.totalDescartes = totalDescartes; }

    public int getTotalActualizaciones() { return totalActualizaciones; }
    public void setTotalActualizaciones(int totalActualizaciones) { this.totalActualizaciones = totalActualizaciones; }

    public long getTiempoMs() { return tiempoMs; }
    public void setTiempoMs(long tiempoMs) { this.tiempoMs = tiempoMs; }

    public String getNombreAlgoritmo() { return nombreAlgoritmo; }

    public String getRutaFormateada() {
        if (!alcanzable) return "❌ No existe camino entre los nodos seleccionados.";
        if (cicloNegativo) return "⚠️ Se detectó un ciclo negativo en el grafo.";
        if (ruta == null || ruta.isEmpty()) return "Sin ruta.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            sb.append(ruta.get(i).getNombre());
            if (i < ruta.size() - 1) sb.append(" → ");
        }
        sb.append("  |  Costo total: ").append(costoTotal);
        return sb.toString();
    }
}
