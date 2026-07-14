package Vista;

import Controlador.GestorCitas;
import Modelo.Cita;
import Observer.Observador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardCitasPanel extends JPanel implements Observador {

    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;
    private Runnable onCitaFinalizada;

    public DashboardCitasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        GestorCitas.getInstancia().suscribir(this);

        iniciarAutoRefresh(); // ¡Nuevo! Auto-refresco
        cargarDatos();
    }

    private void iniciarAutoRefresh() {
        // Refresca la tabla cada 10 segundos en segundo plano sin congelar la app
        new Timer(10000, e -> cargarDatos()).start();
    }

    public void setOnCitaFinalizada(Runnable callback) {
        this.onCitaFinalizada = callback;
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Dashboard de Citas Vigentes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Paciente", "Medico", "Fecha/Hora", "Motivo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setRowHeight(26);
        tablaCitas.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tablaCitas), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton btnConsultorio = new JButton("En Consultorio");
        JButton btnAtendido    = new JButton("Marcar Atendido");
        JButton btnCancelar    = new JButton("Cancelar Cita");
        lblEstado = new JLabel("Selecciona una cita y cambia su estado.");
        lblEstado.setFont(new Font("SansSerif", Font.ITALIC, 12));

        btnConsultorio.addActionListener(e -> cambiarEstadoCitaSeleccionada(Cita.Estado.EN_CONSULTORIO));
        btnAtendido.addActionListener(e    -> cambiarEstadoCitaSeleccionada(Cita.Estado.ATENDIDO));
        btnCancelar.addActionListener(e    -> cambiarEstadoCitaSeleccionada(Cita.Estado.CANCELADO));

        panelBotones.add(btnConsultorio);
        panelBotones.add(btnAtendido);
        panelBotones.add(btnCancelar);
        panelBotones.add(lblEstado);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cambiarEstadoCitaSeleccionada(Cita.Estado estado) {
        int fila = tablaCitas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);

        // Petición asíncrona al API para cambiar el estado
        GestorCitas.getInstancia().cambiarEstado(id, estado)
                .thenAccept(v -> SwingUtilities.invokeLater(() -> {
                    // Aquí el API ya confirmó el cambio. Se refresca la tabla.
                    cargarDatos();
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "No se pudo actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                    );
                    return null;
                });
    }

    @Override
    public void actualizar(String nuevoEstado, int citaId) {
        cargarDatos();
        lblEstado.setText("Cita #" + citaId + " -> " + nuevoEstado);
        boolean esFinal = "ATENDIDO".equals(nuevoEstado) || "CANCELADO".equals(nuevoEstado);
        if (esFinal && onCitaFinalizada != null) {
            onCitaFinalizada.run();
        }
    }

    public void cargarDatos() {
        // Petición asíncrona por Tailscale
        GestorCitas.getInstancia().getCitasVigentes()
                .thenAccept(citas -> SwingUtilities.invokeLater(() -> {
                    modeloTabla.setRowCount(0);
                    for (Cita c : citas) {
                        modeloTabla.addRow(new Object[]{
                                c.getId(), c.getPaciente().getNombre(), c.getMedico(),
                                c.getFechaFormateada(), c.getMotivo(), c.getEstado().name()
                        });
                    }
                }))
                .exceptionally(ex -> {
                    System.err.println("Error cargando dashboard: " + ex.getMessage());
                    return null;
                });
    }
}