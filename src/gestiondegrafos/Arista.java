package gestiondegrafos;

public class Arista {
    private Vertice origen;
    private Vertice destino;
    private double peso;

    public Arista(Vertice origen, Vertice destino, double peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public Vertice getOrigen() {
        return origen;
    }

    public Vertice getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    @Override
    public String toString() {
        return origen.getNombre() + " --[" + peso + "]--> " + destino.getNombre();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Arista)) return false;
        Arista otra = (Arista) obj;
        return this.origen.equals(otra.origen) && this.destino.equals(otra.destino);
    }

    @Override
    public int hashCode() {
        return 31 * origen.hashCode() + destino.hashCode();
    }
}
