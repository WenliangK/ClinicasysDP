package Vista;

import Controlador.GestorMedicos;
import Modelo.Medico;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MedicosPanel extends JPanel {
    private final GestorMedicos gestor = GestorMedicos.getInstancia();
    private final List<Medico> medicos = new ArrayList<>();

    private JTable tablaMedicos;
    private DefaultTableModel modeloTabla;

    public MedicosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        iniciarAutoRefresh();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Directorio de Médicos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre del Médico", "Especialidad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaMedicos = new JTable(modeloTabla);
        tablaMedicos.setRowHeight(26);
        tablaMedicos.setAutoCreateRowSorter(true);
        tablaMedicos.getTableHeader().setReorderingAllowed(false);

        add(new JScrollPane(tablaMedicos), BorderLayout.CENTER);

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
        GestorMedicos.getInstancia().getTodos().thenAccept(lista -> {
            SwingUtilities.invokeLater(() -> {
                modeloTabla.setRowCount(0);
                for (Medico m : lista) {
                    modeloTabla.addRow(new Object[]{
                            m.getId(), m.getNombre(), m.getEspecialidad()
                    });
                }
            });
        }).exceptionally(ex -> {
            System.err.println("Error de red en médicos: " + ex.getMessage());
            return null;
        });
    }
}
