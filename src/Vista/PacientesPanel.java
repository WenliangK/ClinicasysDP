package Vista;

import Controlador.GestorPacientes;
import Modelo.Paciente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PacientesPanel extends JPanel {

    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;

    public PacientesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        iniciarAutoRefresh();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Directorio de Pacientes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Eliminamos "Apellido" para que coincida con tu modelo real
        String[] columnas = {"ID", "Nombre Completo", "DNI", "Teléfono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setRowHeight(26);
        tablaPacientes.getTableHeader().setReorderingAllowed(false);

        add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnRefrescar = new JButton("Refrescar Manualmente");
        btnRefrescar.addActionListener(e -> cargarTabla());
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void iniciarAutoRefresh() {
        new Timer(10000, e -> cargarTabla()).start();
    }

    public void cargarTabla() {
        GestorPacientes.getInstancia().listarPacientes().thenAccept(lista -> {
            SwingUtilities.invokeLater(() -> {
                modeloTabla.setRowCount(0);
                for (Paciente p : lista) {
                    modeloTabla.addRow(new Object[]{
                            // Asumo estos son tus métodos reales
                            p.getId(), p.getNombre(), p.getDni(), p.getTelefono()
                    });
                }
            });
        }).exceptionally(ex -> {
            System.err.println("Error de red en pacientes: " + ex.getMessage());
            return null;
        });
    }
}