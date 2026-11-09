package gestiondegrafos;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class PanelGrafo extends JPanel {

    private static final Color COLOR_FONDO          = new Color(255, 255, 255);
    private static final Color COLOR_VERTICE        = new Color(100, 149, 237);
    private static final Color COLOR_VERTICE_BORDE  = new Color(30, 80, 160);
    private static final Color COLOR_VERTICE_ORIGEN = new Color(60, 179, 113);
    private static final Color COLOR_VERTICE_DEST   = new Color(205, 92, 92);
    private static final Color COLOR_ARISTA         = new Color(120, 120, 120);
    private static final Color COLOR_CAMINO         = new Color(220, 50, 50);
    private static final Color COLOR_PESO           = new Color(60, 60, 60);
    private static final Color COLOR_NOMBRE         = Color.WHITE;
    private static final Font  FUENTE_NOMBRE        = new Font("Dialog", Font.BOLD, 11);
    private static final Font  FUENTE_PESO          = new Font("Dialog", Font.PLAIN, 10);

    private static final int RADIO_VERTICE = 22;

    private GrafoPonderado grafo;
    private Map<Vertice, Point> posiciones;
    private List<Vertice> camino;
    private String origenNombre;
    private String destinoNombre;

    public PanelGrafo() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 400));
        posiciones = new HashMap<>();
        camino = new ArrayList<>();
    }

    public void setGrafo(GrafoPonderado grafo) {
        this.grafo = grafo;
        recalcularPosiciones();
        repaint();
    }

    public void setCamino(List<Vertice> camino, String origen, String destino) {
        this.camino = (camino != null) ? camino : new ArrayList<>();
        this.origenNombre = origen;
        this.destinoNombre = destino;
        repaint();
    }

    public void limpiarCamino() {
        this.camino = new ArrayList<>();
        repaint();
    }

    private void recalcularPosiciones() {
        posiciones.clear();
        if (grafo == null) return;

        List<Vertice> vertices = grafo.getVertices();
        int n = vertices.size();
        if (n == 0) return;

        int w = Math.max(getWidth(), 600);
        int h = Math.max(getHeight(), 400);
        int cx = w / 2;
        int cy = h / 2;
        int radio = Math.min(w, h) / 2 - 60;

        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2;
            int x = (int) (cx + radio * Math.cos(angulo));
            int y = (int) (cy + radio * Math.sin(angulo));
            posiciones.put(vertices.get(i), new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grafo == null) {
            drawEmpty(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (posiciones.size() != grafo.getVertices().size()) {
            recalcularPosiciones();
        }

        Set<Arista> aristasEnCamino = getAristasEnCamino();

        for (Arista arista : grafo.getAristas()) {
            Point p1 = posiciones.get(arista.getOrigen());
            Point p2 = posiciones.get(arista.getDestino());
            if (p1 == null || p2 == null) continue;

            boolean esCaminoOptimo = aristasEnCamino.contains(arista);

            if (esCaminoOptimo) {
                g2.setColor(COLOR_CAMINO);
                g2.setStroke(new BasicStroke(3.5f));
            } else {
                g2.setColor(COLOR_ARISTA);
                g2.setStroke(new BasicStroke(1.5f));
            }

            dibujarAristaConFlecha(g2, p1, p2, arista.getPeso(), esCaminoOptimo, grafo.esDirigido());
        }

        for (Vertice v : grafo.getVertices()) {
            Point p = posiciones.get(v);
            if (p == null) continue;

            Color colorRelleno;
            if (v.getNombre().equalsIgnoreCase(origenNombre)) {
                colorRelleno = COLOR_VERTICE_ORIGEN;
            } else if (v.getNombre().equalsIgnoreCase(destinoNombre)) {
                colorRelleno = COLOR_VERTICE_DEST;
            } else if (camino.contains(v)) {
                colorRelleno = new Color(255, 165, 0);
            } else {
                colorRelleno = COLOR_VERTICE;
            }

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(p.x - RADIO_VERTICE + 3, p.y - RADIO_VERTICE + 3,
                        RADIO_VERTICE * 2, RADIO_VERTICE * 2);

            g2.setColor(colorRelleno);
            g2.fillOval(p.x - RADIO_VERTICE, p.y - RADIO_VERTICE,
                        RADIO_VERTICE * 2, RADIO_VERTICE * 2);

            g2.setColor(COLOR_VERTICE_BORDE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(p.x - RADIO_VERTICE, p.y - RADIO_VERTICE,
                        RADIO_VERTICE * 2, RADIO_VERTICE * 2);

            g2.setFont(FUENTE_NOMBRE);
            g2.setColor(COLOR_NOMBRE);
            FontMetrics fm = g2.getFontMetrics();
            String nombre = v.getNombre();
            if (nombre.length() > 4) nombre = nombre.substring(0, 4);
            int tx = p.x - fm.stringWidth(nombre) / 2;
            int ty = p.y + fm.getAscent() / 2 - 1;
            g2.drawString(nombre, tx, ty);
        }

        dibujarLeyenda(g2);
    }

    private void dibujarAristaConFlecha(Graphics2D g2, Point p1, Point p2,
                                         double peso, boolean resaltada, boolean dirigido) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1) return;

        double ux = dx / dist;
        double uy = dy / dist;

        int x1 = (int) (p1.x + ux * RADIO_VERTICE);
        int y1 = (int) (p1.y + uy * RADIO_VERTICE);
        int x2 = (int) (p2.x - ux * RADIO_VERTICE);
        int y2 = (int) (p2.y - uy * RADIO_VERTICE);

        g2.drawLine(x1, y1, x2, y2);

        if (dirigido) {
            dibujarPuntaFlecha(g2, x1, y1, x2, y2);
        }

        int mx = (x1 + x2) / 2;
        int my = (y1 + y2) / 2;
        g2.setFont(FUENTE_PESO);
        String pesoStr = String.valueOf((int) peso == peso ? (int) peso : peso);

        FontMetrics fm = g2.getFontMetrics();
        int pw = fm.stringWidth(pesoStr) + 6;
        int ph = fm.getHeight();
        g2.setColor(new Color(15, 20, 40, 200));
        g2.fillRoundRect(mx - pw / 2, my - ph + 2, pw, ph, 4, 4);

        g2.setColor(resaltada ? COLOR_CAMINO : COLOR_PESO);
        g2.drawString(pesoStr, mx - fm.stringWidth(pesoStr) / 2, my);
    }

    private void dibujarPuntaFlecha(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int tam = 10;
        double a1 = angulo - Math.PI / 7;
        double a2 = angulo + Math.PI / 7;
        int ax1 = (int) (x2 - tam * Math.cos(a1));
        int ay1 = (int) (y2 - tam * Math.sin(a1));
        int ax2 = (int) (x2 - tam * Math.cos(a2));
        int ay2 = (int) (y2 - tam * Math.sin(a2));
        g2.fillPolygon(new int[]{x2, ax1, ax2}, new int[]{y2, ay1, ay2}, 3);
    }

    private Set<Arista> getAristasEnCamino() {
        Set<Arista> set = new HashSet<>();
        if (camino == null || camino.size() < 2) return set;
        for (int i = 0; i < camino.size() - 1; i++) {
            Arista a = grafo.buscarArista(camino.get(i).getNombre(), camino.get(i + 1).getNombre());
            if (a != null) set.add(a);
        }
        return set;
    }

    private void drawEmpty(Graphics g) {
        g.setColor(Color.GRAY);
        g.setFont(new Font("Dialog", Font.ITALIC, 13));
        String msg = "Agregue vértices y aristas para visualizar el grafo";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int x = 8, y = getHeight() - 76;
        g2.setFont(new Font("Dialog", Font.PLAIN, 10));
        int sh = 12;

        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(x - 4, y - 12, 148, 72, 6, 6);
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x - 4, y - 12, 148, 72, 6, 6);

        g2.setColor(COLOR_VERTICE_ORIGEN);
        g2.fillOval(x, y, sh, sh);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Nodo Origen", x + sh + 4, y + sh - 1);
        y += sh + 4;

        g2.setColor(COLOR_VERTICE_DEST);
        g2.fillOval(x, y, sh, sh);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Nodo Destino", x + sh + 4, y + sh - 1);
        y += sh + 4;

        g2.setColor(new Color(255, 165, 0));
        g2.fillOval(x, y, sh, sh);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("En camino optimo", x + sh + 4, y + sh - 1);
        y += sh + 4;

        g2.setColor(COLOR_CAMINO);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(x, y + sh / 2, x + sh, y + sh / 2);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Ruta optima", x + sh + 4, y + sh - 1);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        recalcularPosiciones();
    }
}
