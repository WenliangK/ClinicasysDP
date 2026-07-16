package Vista;

import Controlador.GestorMedicos;
import Modelo.Medico;
import Utilidades.RespuestaHttp;
import Utilidades.Validador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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

public class    MedicosPanel extends JPanel {
    private final GestorMedicos gestor = GestorMedicos.getInstancia();
    private final List<Medico> medicos = new ArrayList<>();

    private JTable tablaMedicos;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;
    private JButton btnCambiarEstado;

    public MedicosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        new Timer(10000, e -> cargarTabla()).start();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Directorio de Médicos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre del médico", "Especialidad", "Tipo", "Estado"};
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
        tablaMedicos.getSelectionModel().addListSelectionListener(e -> actualizarBotonEstado());
        tablaMedicos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaMedicos.getSelectedRow() >= 0) {
                    editarSeleccionado();
                }
            }
        });
        add(new JScrollPane(tablaMedicos), BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnNuevo = new JButton("Nuevo médico");
        JButton btnEditar = new JButton("Editar");
        btnCambiarEstado = new JButton("Desactivar");
        JButton btnRefrescar = new JButton("Refrescar");

        btnNuevo.addActionListener(e -> mostrarFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoSeleccionado());
        btnRefrescar.addActionListener(e -> cargarTabla());

        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnCambiarEstado);
        botones.add(btnRefrescar);
        lblEstado = new JLabel(" ");
        pie.add(botones, BorderLayout.WEST);
        pie.add(lblEstado, BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);
    }

    public void cargarTabla() {
        lblEstado.setText("Cargando...");
        gestor.listarMedicos()
                .thenAccept(lista -> SwingUtilities.invokeLater(() -> {
                    medicos.clear();
                    medicos.addAll(lista);
                    modeloTabla.setRowCount(0);
                    for (Medico medico : medicos) {
                        modeloTabla.addRow(new Object[]{
                                medico.getId(),
                                medico.getNombre(),
                                medico.getEspecialidad(),
                                medico.getTipo(),
                                medico.isActivo() ? "ACTIVO" : "INACTIVO"
                        });
                    }
                    lblEstado.setText(medicos.size() + " médico(s)");
                    actualizarBotonEstado();
                }))
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> lblEstado.setText("Error: " + RespuestaHttp.mensaje(error)));
                    return null;
                });
    }

    private void editarSeleccionado() {
        Medico medico = obtenerSeleccionado();
        if (medico == null) {
            mostrarAvisoSeleccion();
            return;
        }
        mostrarFormulario(medico);
    }

    private void mostrarFormulario(Medico actual) {
        JTextField txtNombre = new JTextField(actual == null ? "" : actual.getNombre(), 24);
        JTextField txtEspecialidad = new JTextField(actual == null ? "" : actual.getEspecialidad(), 20);
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"PRIVADO", "PUBLICO"});
        if (actual != null && actual.getTipo() != null) {
            cbTipo.setSelectedItem(actual.getTipo());
        }

        JPanel formulario = new JPanel(new GridBagLayout());
        agregarCampo(formulario, 0, "Nombre completo:", txtNombre);
        agregarCampo(formulario, 1, "Especialidad:", txtEspecialidad);
        agregarCampo(formulario, 2, "Tipo de atención:", cbTipo);

        while (true) {
            String titulo = actual == null ? "Nuevo médico" : "Editar médico";
            int opcion = JOptionPane.showConfirmDialog(this, formulario, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }
            if (txtNombre.getText().trim().isEmpty()) {
                Validador.mostrarError(this, "Ingresa el nombre completo del médico.");
                continue;
            }
            if (txtEspecialidad.getText().trim().isEmpty()) {
                Validador.mostrarError(this, "Ingresa la especialidad.");
                continue;
            }

            Medico medico = new Medico(
                    actual == null ? null : actual.getId(),
                    txtNombre.getText().trim(),
                    txtEspecialidad.getText().trim(),
                    (String) cbTipo.getSelectedItem(),
                    actual == null || actual.isActivo()
            );
            guardarMedico(medico);
            return;
        }
    }

    private void guardarMedico(Medico medico) {
        lblEstado.setText("Guardando médico...");
        gestor.guardar(medico)
                .thenAccept(guardado -> SwingUtilities.invokeLater(() -> {
                    Validador.mostrarExito(this, "Médico guardado correctamente.");
                    cargarTabla();
                }))
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText("No se pudo guardar");
                        Validador.mostrarError(this, RespuestaHttp.mensaje(error));
                    });
                    return null;
                });
    }

    private void cambiarEstadoSeleccionado() {
        Medico medico = obtenerSeleccionado();
        if (medico == null) {
            mostrarAvisoSeleccion();
            return;
        }
        boolean nuevoEstado = !medico.isActivo();
        String accion = nuevoEstado ? "reactivar" : "desactivar";
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Deseas " + accion + " a " + medico.getNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        gestor.cambiarActivo(medico.getId(), nuevoEstado)
                .thenAccept(guardado -> SwingUtilities.invokeLater(this::cargarTabla))
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> Validador.mostrarError(this, RespuestaHttp.mensaje(error)));
                    return null;
                });
    }

    private Medico obtenerSeleccionado() {
        int filaVista = tablaMedicos.getSelectedRow();
        if (filaVista < 0) {
            return null;
        }
        int filaModelo = tablaMedicos.convertRowIndexToModel(filaVista);
        return filaModelo < medicos.size() ? medicos.get(filaModelo) : null;
    }

    private void actualizarBotonEstado() {
        Medico medico = obtenerSeleccionado();
        btnCambiarEstado.setText(medico != null && !medico.isActivo() ? "Reactivar" : "Desactivar");
    }

    private void mostrarAvisoSeleccion() {
        JOptionPane.showMessageDialog(this, "Selecciona un médico primero.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private static void agregarCampo(JPanel panel, int fila, String etiqueta, java.awt.Component campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }
}
