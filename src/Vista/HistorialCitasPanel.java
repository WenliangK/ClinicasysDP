package Vista;

import Controlador.GestorCitas;
import Modelo.Cita;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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

        String[] columnas = {"ID", "Paciente", "Medico", "Fecha/Hora Cita", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloTabla);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        add(btnRefrescar, BorderLayout.SOUTH);
    }

    public void cargarDatos() {
        // Llamada asíncrona a través del Gestor (3 capas)
        GestorCitas.getInstancia().getTodas().thenAccept(historial -> {
            SwingUtilities.invokeLater(() -> {
                modeloTabla.setRowCount(0);
                for (Cita c : historial) {
                    modeloTabla.addRow(new Object[]{
                            c.getId(),
                            c.getPaciente() != null ? c.getPaciente().getNombre() : "N/A",
                            c.getMedico(),
                            c.getFechaHora(),
                            c.getEstado()
                    });
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error de red: " + ex.getMessage()));
            return null;
        });
    }
}
