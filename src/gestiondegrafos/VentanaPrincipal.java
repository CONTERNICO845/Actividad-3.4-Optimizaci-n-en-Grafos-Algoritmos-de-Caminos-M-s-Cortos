package gestiondegrafos;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class VentanaPrincipal extends JFrame {

    private GrafoPonderado grafo = new GrafoPonderado(false);
    private ResultadoCamino ultimoDijkstra;
    private ResultadoCamino ultimoBellman;

    private PanelGrafo panelGrafo;

    private JTextField txtNodoNombre;
    private JTextField txtElimNodo;
    private JTextField txtAristaOrigen;
    private JTextField txtAristaDestino;
    private JTextField txtAristaPeso;
    private JTextField txtElimAristaOri;
    private JTextField txtElimAristaDes;
    private JCheckBox  chkDirigido;
    private JTextArea  txtInfoGrafo;
    private DefaultTableModel modeloTablaAristas;
    private JTable     tablaAristas;

    private JComboBox<String> cmbOrigen;
    private JComboBox<String> cmbDestino;
    private JRadioButton rbDijkstra;
    private JRadioButton rbBellman;
    private JTextArea    txtTraza;
    private JTextArea    txtResultado;

    private JLabel lblEstado;

    public VentanaPrincipal() {
        initComponents();
        cargarGrafoDemo();
    }

    private void initComponents() {
        setTitle("Gestión y Optimización de Grafos Ponderados – Act. 3.3 / 3.4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(4, 4));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelIzquierdo(), crearPanelDerecho());
        split.setDividerLocation(420);
        split.setResizeWeight(0.38);
        split.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        add(crearMenuBar(),  BorderLayout.NORTH);
        add(split,           BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    private JPanel crearMenuBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        p.setBackground(new Color(240, 240, 240));
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel titulo = new JLabel("Grafos Ponderados – Caminos Más Cortos");
        titulo.setFont(new Font("Dialog", Font.BOLD, 14));
        p.add(titulo, BorderLayout.WEST);

        JLabel sub = new JLabel("Algoritmos: Dijkstra  |  Bellman-Ford   ");
        sub.setFont(new Font("Dialog", Font.PLAIN, 11));
        sub.setForeground(Color.GRAY);
        p.add(sub, BorderLayout.EAST);
        return p;
    }

    private JPanel crearBarraEstado() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        p.setBackground(new Color(240, 240, 240));
        lblEstado = new JLabel("  Listo. Grafo de demostración cargado.");
        lblEstado.setFont(new Font("Dialog", Font.PLAIN, 11));
        p.add(lblEstado, BorderLayout.WEST);
        return p;
    }

    private JTabbedPane crearPanelIzquierdo() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Gestión del Grafo",     crearTabGestion());
        tabs.addTab("Optimización de Rutas", crearTabOptimizacion());
        return tabs;
    }

    private JPanel crearTabGestion() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        p.add(crearPanelTipoGrafo());
        p.add(Box.createVerticalStrut(6));
        p.add(crearPanelVertices());
        p.add(Box.createVerticalStrut(6));
        p.add(crearPanelAristas());
        p.add(Box.createVerticalStrut(6));
        p.add(crearPanelTablaAristas());
        return p;
    }

    private JPanel crearPanelTipoGrafo() {
        JPanel p = titulado("Configuración");
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));

        chkDirigido = new JCheckBox("Grafo Dirigido");
        chkDirigido.addActionListener(e -> {
            grafo.setDirigido(chkDirigido.isSelected());
            actualizarTodo();
            estado("Tipo de grafo cambiado.");
        });

        JButton btnLimpiar = boton("Limpiar todo");
        btnLimpiar.addActionListener(e -> limpiarGrafo());

        p.add(chkDirigido);
        p.add(btnLimpiar);
        return p;
    }

    private JPanel crearPanelVertices() {
        JPanel p = titulado("Vértices");
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 4, 3, 4);
        c.fill   = GridBagConstraints.HORIZONTAL;

        txtNodoNombre = new JTextField(10);
        txtElimNodo   = new JTextField(10);
        JButton btnAgregar  = boton("Agregar");
        JButton btnEliminar = boton("Eliminar");

        btnAgregar.addActionListener(e  -> agregarVertice());
        btnEliminar.addActionListener(e -> eliminarVertice());

        c.gridx=0; c.gridy=0; c.weightx=0; p.add(new JLabel("Nombre:"), c);
        c.gridx=1; c.weightx=1;             p.add(txtNodoNombre, c);
        c.gridx=2; c.weightx=0;             p.add(btnAgregar, c);

        c.gridx=0; c.gridy=1; c.weightx=0; p.add(new JLabel("Eliminar:"), c);
        c.gridx=1; c.weightx=1;             p.add(txtElimNodo, c);
        c.gridx=2; c.weightx=0;             p.add(btnEliminar, c);

        return p;
    }

    private JPanel crearPanelAristas() {
        JPanel p = titulado("Aristas");
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 4, 3, 4);
        c.fill   = GridBagConstraints.HORIZONTAL;

        txtAristaOrigen  = new JTextField(7);
        txtAristaDestino = new JTextField(7);
        txtAristaPeso    = new JTextField(4);
        txtElimAristaOri = new JTextField(7);
        txtElimAristaDes = new JTextField(7);
        JButton btnAgregar  = boton("Agregar");
        JButton btnEliminar = boton("Eliminar");

        btnAgregar.addActionListener(e  -> agregarArista());
        btnEliminar.addActionListener(e -> eliminarArista());

        c.gridx=0; c.gridy=0; p.add(new JLabel("Origen:"), c);
        c.gridx=1;             p.add(txtAristaOrigen, c);
        c.gridx=2;             p.add(new JLabel("Destino:"), c);
        c.gridx=3;             p.add(txtAristaDestino, c);
        c.gridx=4;             p.add(new JLabel("Peso:"), c);
        c.gridx=5;             p.add(txtAristaPeso, c);
        c.gridx=6;             p.add(btnAgregar, c);

        c.gridx=0; c.gridy=1; p.add(new JLabel("Origen:"), c);
        c.gridx=1;             p.add(txtElimAristaOri, c);
        c.gridx=2;             p.add(new JLabel("Destino:"), c);
        c.gridx=3;             p.add(txtElimAristaDes, c);
        c.gridx=6;             p.add(btnEliminar, c);

        return p;
    }

    private JPanel crearPanelTablaAristas() {
        JPanel p = titulado("Aristas del grafo");
        p.setLayout(new BorderLayout());

        modeloTablaAristas = new DefaultTableModel(
                new Object[]{"Origen", "Destino", "Peso"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaAristas = new JTable(modeloTablaAristas);
        tablaAristas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAristas.setRowHeight(20);
        tablaAristas.getTableHeader().setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(tablaAristas);
        sp.setPreferredSize(new Dimension(0, 120));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearTabOptimizacion() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        p.add(crearPanelControlesRuta(), BorderLayout.NORTH);

        JSplitPane spV = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                crearPanelTraza(), crearPanelResultado());
        spV.setDividerLocation(280);
        spV.setResizeWeight(0.7);
        p.add(spV, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelControlesRuta() {
        JPanel p = titulado("Selección de ruta y algoritmo");
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill   = GridBagConstraints.HORIZONTAL;

        cmbOrigen  = new JComboBox<>();
        cmbDestino = new JComboBox<>();
        cmbOrigen.setPrototypeDisplayValue("MMMMM");
        cmbDestino.setPrototypeDisplayValue("MMMMM");

        rbDijkstra = new JRadioButton("Dijkstra", true);
        rbBellman  = new JRadioButton("Bellman-Ford");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbDijkstra); bg.add(rbBellman);

        JButton btnEjecutar = boton("▶  Ejecutar");
        JButton btnComparar = boton("⇆  Comparar ambos");
        JButton btnLimpiar  = boton("Limpiar traza");

        btnEjecutar.addActionListener(e -> ejecutarSeleccionado());
        btnComparar.addActionListener(e -> compararAmbos());
        btnLimpiar.addActionListener(e  -> limpiarTraza());

        c.gridx=0; c.gridy=0; c.weightx=0; p.add(new JLabel("Nodo Origen:"), c);
        c.gridx=1; c.weightx=0.3;           p.add(cmbOrigen, c);
        c.gridx=2; c.weightx=0;             p.add(new JLabel("Nodo Destino:"), c);
        c.gridx=3; c.weightx=0.3;           p.add(cmbDestino, c);

        c.gridx=0; c.gridy=1; c.weightx=0; p.add(new JLabel("Algoritmo:"), c);
        JPanel panelRB = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelRB.add(rbDijkstra); panelRB.add(rbBellman);
        c.gridx=1; c.gridwidth=3; c.weightx=1; p.add(panelRB, c);
        c.gridwidth=1;

        c.gridx=0; c.gridy=2; c.weightx=0;   p.add(btnEjecutar, c);
        c.gridx=1; c.weightx=0;               p.add(btnComparar, c);
        c.gridx=2; c.weightx=0;               p.add(btnLimpiar, c);

        return p;
    }

    private JPanel crearPanelTraza() {
        JPanel p = titulado("Traza del algoritmo – paso a paso");
        p.setLayout(new BorderLayout());

        txtTraza = new JTextArea();
        txtTraza.setEditable(false);
        txtTraza.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtTraza.setLineWrap(false);

        JScrollPane sp = new JScrollPane(txtTraza,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelResultado() {
        JPanel p = titulado("Resultado final");
        p.setLayout(new BorderLayout());

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(txtResultado);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelDerecho() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBorder(titulado("Representación visual del grafo").getBorder());

        panelGrafo = new PanelGrafo();
        panelGrafo.setBorder(BorderFactory.createLoweredBevelBorder());
        p.add(panelGrafo, BorderLayout.CENTER);

        JButton btnRedist = boton("Redistribuir nodos");
        btnRedist.addActionListener(e -> panelGrafo.setGrafo(grafo));
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.add(btnRedist);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private void agregarVertice() {
        String nombre = txtNodoNombre.getText().trim();
        if (nombre.isEmpty()) { error("Ingrese un nombre para el vértice."); return; }
        if (grafo.agregarVertice(nombre)) {
            txtNodoNombre.setText("");
            actualizarTodo();
            estado("Vértice '" + nombre + "' agregado.");
        } else {
            error("El vértice '" + nombre + "' ya existe.");
        }
    }

    private void eliminarVertice() {
        String nombre = txtElimNodo.getText().trim();
        if (nombre.isEmpty()) { error("Ingrese el nombre del vértice a eliminar."); return; }
        if (grafo.eliminarVertice(nombre)) {
            txtElimNodo.setText("");
            actualizarTodo();
            estado("Vértice '" + nombre + "' eliminado.");
        } else {
            error("El vértice '" + nombre + "' no existe.");
        }
    }

    private void agregarArista() {
        String ori = txtAristaOrigen.getText().trim();
        String des = txtAristaDestino.getText().trim();
        String pesStr = txtAristaPeso.getText().trim();

        if (ori.isEmpty() || des.isEmpty() || pesStr.isEmpty()) {
            error("Complete los campos Origen, Destino y Peso."); return;
        }
        double peso;
        try { peso = Double.parseDouble(pesStr); }
        catch (NumberFormatException ex) { error("El peso debe ser un número (ej. 5 o 3.5)."); return; }

        if (grafo.buscarVertice(ori) == null)  { error("El vértice origen '"  + ori + "' no existe."); return; }
        if (grafo.buscarVertice(des) == null)  { error("El vértice destino '" + des + "' no existe."); return; }

        if (grafo.agregarArista(ori, des, peso)) {
            txtAristaOrigen.setText(""); txtAristaDestino.setText(""); txtAristaPeso.setText("");
            actualizarTodo();
            estado("Arista " + ori + " -> " + des + " [" + peso + "] agregada.");
        } else {
            error("La arista '" + ori + "' -> '" + des + "' ya existe.");
        }
    }

    private void eliminarArista() {
        String ori = txtElimAristaOri.getText().trim();
        String des = txtElimAristaDes.getText().trim();
        if (ori.isEmpty() || des.isEmpty()) { error("Ingrese origen y destino de la arista."); return; }
        if (grafo.eliminarArista(ori, des)) {
            txtElimAristaOri.setText(""); txtElimAristaDes.setText("");
            actualizarTodo();
            estado("Arista '" + ori + "' -> '" + des + "' eliminada.");
        } else {
            error("No existe esa arista en el grafo.");
        }
    }

    private void limpiarGrafo() {
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar todos los vértices y aristas?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            grafo.limpiar();
            panelGrafo.limpiarCamino();
            limpiarTraza();
            actualizarTodo();
            estado("Grafo limpiado.");
        }
    }

    private void ejecutarSeleccionado() {
        String ori = (String) cmbOrigen.getSelectedItem();
        String des = (String) cmbDestino.getSelectedItem();
        if (!validarOrigenDestino(ori, des)) return;

        if (rbDijkstra.isSelected()) {
            ultimoDijkstra = new AlgoritmoDijkstra(grafo).ejecutar(ori, des);
            mostrarResultado(ultimoDijkstra);
        } else {
            ultimoBellman = new AlgoritmoBellmanFord(grafo).ejecutar(ori, des);
            mostrarResultado(ultimoBellman);
        }
    }

    private void compararAmbos() {
        String ori = (String) cmbOrigen.getSelectedItem();
        String des = (String) cmbDestino.getSelectedItem();
        if (!validarOrigenDestino(ori, des)) return;

        ultimoDijkstra = new AlgoritmoDijkstra(grafo).ejecutar(ori, des);
        ultimoBellman  = new AlgoritmoBellmanFord(grafo).ejecutar(ori, des);

        StringBuilder traza = new StringBuilder();
        traza.append("==========================================================\n");
        traza.append("  EJECUCION COMPARATIVA   Origen: ").append(ori)
             .append("   Destino: ").append(des).append("\n");
        traza.append("==========================================================\n\n");

        traza.append("--- DIJKSTRA ---\n\n");
        traza.append(ultimoDijkstra.getTraza());
        traza.append("\n\n--- BELLMAN-FORD ---\n\n");
        traza.append(ultimoBellman.getTraza());
        traza.append("\n\n");
        traza.append(generarComparativa(ultimoDijkstra, ultimoBellman));

        txtTraza.setText(traza.toString());
        txtTraza.setCaretPosition(0);

        ResultadoCamino mostrar = ultimoDijkstra.isAlcanzable() ? ultimoDijkstra : ultimoBellman;
        mostrarResultadoFinal(mostrar);

        if (mostrar.isAlcanzable())
            panelGrafo.setCamino(mostrar.getRuta(), ori, des);
        else
            panelGrafo.limpiarCamino();

        estado("Comparativa ejecutada. Origen: " + ori + " - Destino: " + des);
    }

    private void mostrarResultado(ResultadoCamino r) {
        txtTraza.setText(r.getTraza());
        txtTraza.setCaretPosition(0);
        mostrarResultadoFinal(r);

        String ori = (String) cmbOrigen.getSelectedItem();
        String des = (String) cmbDestino.getSelectedItem();
        if (r.isAlcanzable())
            panelGrafo.setCamino(r.getRuta(), ori, des);
        else
            panelGrafo.limpiarCamino();

        estado(r.getNombreAlgoritmo() + " ejecutado. Origen: " + ori + " - Destino: " + des);
    }

    private void mostrarResultadoFinal(ResultadoCamino r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Algoritmo : ").append(r.getNombreAlgoritmo()).append("\n");
        sb.append("----------------------------------------------------------\n");

        if (!r.isAlcanzable()) {
            if (r.isCicloNegativo()) {
                sb.append("CICLO NEGATIVO DETECTADO\n\n");
                sb.append("El grafo contiene un ciclo de peso negativo.\n");
                sb.append("Bellman-Ford lo ha detectado correctamente.\n");
            } else {
                sb.append("NO EXISTE CAMINO\n\n");
                sb.append("Los nodos seleccionados no estan conectados\n");
                sb.append("en el grafo.\n");
            }
        } else {
            sb.append("RUTA OPTIMA ENCONTRADA\n\n");
            sb.append("Secuencia de nodos:\n");
            List<Vertice> ruta = r.getRuta();
            for (int i = 0; i < ruta.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(ruta.get(i).getNombre()).append("\n");
                if (i < ruta.size() - 1) sb.append("      |\n");
            }
            sb.append("\nCosto total : ").append(r.getCostoTotal()).append("\n");
            sb.append("----------------------------------------------------------\n");
            sb.append("Evaluaciones : ").append(r.getTotalEvaluaciones()).append("\n");
            sb.append("Aceptadas    : ").append(r.getTotalActualizaciones()).append("\n");
            sb.append("Descartadas  : ").append(r.getTotalDescartes()).append("\n");
            sb.append("Tiempo       : ").append(r.getTiempoMs()).append(" ms\n");
        }

        txtResultado.setText(sb.toString());
        txtResultado.setCaretPosition(0);
    }

    private String generarComparativa(ResultadoCamino d, ResultadoCamino b) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================================\n");
        sb.append("  COMPARATIVA DE DESEMPENO\n");
        sb.append("==========================================================\n");
        sb.append(String.format("  %-28s %-12s %-12s\n", "Metrica", "Dijkstra", "Bellman-Ford"));
        sb.append("  ----------------------------------------------------------\n");

        String cd = d.isAlcanzable() ? String.valueOf(d.getCostoTotal()) : "N/A";
        String cb = b.isAlcanzable() ? String.valueOf(b.getCostoTotal()) : "N/A";
        sb.append(String.format("  %-28s %-12s %-12s\n", "Costo optimo", cd, cb));
        sb.append(String.format("  %-28s %-12d %-12d\n", "Aristas evaluadas",
                d.getTotalEvaluaciones(), b.getTotalEvaluaciones()));
        sb.append(String.format("  %-28s %-12d %-12d\n", "Relajaciones aceptadas",
                d.getTotalActualizaciones(), b.getTotalActualizaciones()));
        sb.append(String.format("  %-28s %-12d %-12d\n", "Alternativas descartadas",
                d.getTotalDescartes(), b.getTotalDescartes()));
        sb.append(String.format("  %-28s %-12d %-12d\n", "Tiempo (ms)",
                d.getTiempoMs(), b.getTiempoMs()));
        sb.append("  ----------------------------------------------------------\n");

        if (d.isAlcanzable() && b.isAlcanzable()) {
            if (Math.abs(d.getCostoTotal() - b.getCostoTotal()) < 0.0001)
                sb.append("  RESULTADO: Ambos algoritmos obtuvieron el mismo costo optimo.\n");
            else
                sb.append("  ATENCION: Los algoritmos difieren en el costo encontrado.\n");
        } else if (!d.isAlcanzable() && !b.isAlcanzable()) {
            sb.append("  RESULTADO: Ambos confirman que no existe ruta entre los nodos.\n");
        }
        sb.append("==========================================================\n");
        return sb.toString();
    }

    private void limpiarTraza() {
        txtTraza.setText("");
        txtResultado.setText("");
        panelGrafo.limpiarCamino();
        ultimoDijkstra = null;
        ultimoBellman  = null;
        estado("Traza limpiada.");
    }

    private boolean validarOrigenDestino(String ori, String des) {
        if (grafo.getVertices().isEmpty()) {
            error("El grafo no tiene vértices."); return false;
        }
        if (ori == null || ori.isEmpty()) { error("Seleccione un nodo origen."); return false; }
        if (des == null || des.isEmpty()) { error("Seleccione un nodo destino."); return false; }
        if (grafo.buscarVertice(ori) == null) { error("El nodo origen '" + ori + "' no existe."); return false; }
        if (grafo.buscarVertice(des) == null) { error("El nodo destino '" + des + "' no existe."); return false; }
        return true;
    }

    private void actualizarTodo() {
        panelGrafo.setGrafo(grafo);
        actualizarCombos();
        actualizarTabla();
    }

    private void actualizarCombos() {
        String selOri = (String) cmbOrigen.getSelectedItem();
        String selDes = (String) cmbDestino.getSelectedItem();
        cmbOrigen.removeAllItems();
        cmbDestino.removeAllItems();
        for (Vertice v : grafo.getVertices()) {
            cmbOrigen.addItem(v.getNombre());
            cmbDestino.addItem(v.getNombre());
        }
        if (selOri != null) cmbOrigen.setSelectedItem(selOri);
        if (selDes != null) cmbDestino.setSelectedItem(selDes);
    }

    private void actualizarTabla() {
        modeloTablaAristas.setRowCount(0);
        for (Arista a : grafo.getAristas()) {
            if (!grafo.esDirigido() &&
                a.getOrigen().getNombre().compareTo(a.getDestino().getNombre()) > 0) continue;
            modeloTablaAristas.addRow(new Object[]{
                a.getOrigen().getNombre(),
                a.getDestino().getNombre(),
                a.getPeso()
            });
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void estado(String msg) {
        lblEstado.setText("  " + msg);
    }

    private JPanel titulado(String titulo) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), titulo));
        return p;
    }

    private JButton boton(String texto) {
        return new JButton(texto);
    }

    private void cargarGrafoDemo() {
        for (String v : new String[]{"A","B","C","D","E","F","G"})
            grafo.agregarVertice(v);

        grafo.agregarArista("A","B",4);
        grafo.agregarArista("A","C",2);
        grafo.agregarArista("B","C",1);
        grafo.agregarArista("B","D",5);
        grafo.agregarArista("C","D",8);
        grafo.agregarArista("C","E",10);
        grafo.agregarArista("D","E",2);
        grafo.agregarArista("D","F",6);
        grafo.agregarArista("E","G",3);
        grafo.agregarArista("F","G",1);

        actualizarTodo();
        cmbOrigen.setSelectedItem("A");
        cmbDestino.setSelectedItem("G");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}
