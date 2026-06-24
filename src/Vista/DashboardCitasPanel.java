package Vista;

import Controlador.GestorCitas;
import Modelo.Cita;
import Observer.Observador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel del Dashboard de citas.
 * Implementa OBSERVADOR: cuando GestorCitas notifica un cambio de estado,
 * este panel actualiza su JTable automaticamente sin ser invocado directamente.
 */
public class DashboardCitasPanel extends JPanel implements Observador {

    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;

    public DashboardCitasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        // Suscribirse al GestorCitas como observador
        GestorCitas.getInstancia().suscribir(this);
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Titulo
        JLabel titulo = new JLabel("Dashboard de Citas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Paciente", "Medico", "Fecha/Hora", "Motivo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setRowHeight(26);
        tablaCitas.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tablaCitas), BorderLayout.CENTER);

        // Panel inferior con botones de cambio de estado
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
            JOptionPane.showMessageDialog(this, "Selecciona una cita primero.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        GestorCitas.getInstancia().cambiarEstado(id, estado);
    }

    /** Llamado automaticamente por GestorCitas cuando hay un cambio. */
    @Override
    public void actualizar(String nuevoEstado, int citaId) {
        cargarDatos();
        lblEstado.setText("Cita #" + citaId + " -> " + nuevoEstado);
    }

    public void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Cita> citas = GestorCitas.getInstancia().getCitas();
        for (Cita c : citas) {
            modeloTabla.addRow(new Object[]{
                    c.getId(),
                    c.getPaciente().getNombre(),
                    c.getMedico(),
                    c.getFechaFormateada(),
                    c.getMotivo(),
                    c.getEstado().name()
            });
        }
    }
}
