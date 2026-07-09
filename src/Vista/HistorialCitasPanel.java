package Vista;

import Controlador.GestorCitas;
import Modelo.Cita;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Historial de citas ya finalizadas (ATENDIDO o CANCELADO), ordenadas por ID.
 * Incluye la fecha/hora en que la cita fue atendida o cancelada.
 */
public class HistorialCitasPanel extends JPanel {

    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    public HistorialCitasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Historial de Citas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Paciente", "Medico", "Fecha/Hora Cita", "Estado", "Fecha/Hora Atencion o Cancelacion"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(26);
        tablaHistorial.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void cargarDatos() {
        try {
            modeloTabla.setRowCount(0);
            List<Cita> historial = GestorCitas.getInstancia().getHistorial();
            for (Cita c : historial) {
                modeloTabla.addRow(new Object[]{
                        c.getId(),
                        c.getPaciente().getNombre(),
                        c.getMedico(),
                        c.getFechaFormateada(),
                        c.getEstado().name(),
                        c.getFechaActualizacionFormateada()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el historial de citas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}