package Vista;

import Controlador.GestorPacientes;
import Modelo.Paciente;
import Utilidades.RespuestaHttp;
import Utilidades.Validador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class PacientesPanel extends JPanel {
    private final GestorPacientes gestor = GestorPacientes.getInstancia();
    private final List<Paciente> pacientes = new ArrayList<>();

    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;
    private JButton btnCambiarEstado;

    public PacientesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        new Timer(10000, e -> cargarTabla()).start();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Directorio de Pacientes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre completo", "DNI", "Teléfono", "Correo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setRowHeight(26);
        tablaPacientes.setAutoCreateRowSorter(true);
        tablaPacientes.getTableHeader().setReorderingAllowed(false);
        tablaPacientes.getSelectionModel().addListSelectionListener(e -> actualizarBotonEstado());
        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaPacientes.getSelectedRow() >= 0) {
                    editarSeleccionado();
                }
            }
        });
        add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnNuevo = new JButton("Nuevo paciente");
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
        gestor.listarPacientes()
                .thenAccept(lista -> SwingUtilities.invokeLater(() -> {
                    pacientes.clear();
                    pacientes.addAll(lista);
                    modeloTabla.setRowCount(0);
                    for (Paciente paciente : pacientes) {
                        modeloTabla.addRow(new Object[]{
                                paciente.getId(),
                                paciente.getNombre(),
                                paciente.getDni(),
                                paciente.getTelefono(),
                                paciente.getEmail() == null ? "" : paciente.getEmail(),
                                paciente.isActivo() ? "ACTIVO" : "INACTIVO"
                        });
                    }
                    lblEstado.setText(pacientes.size() + " paciente(s)");
                    actualizarBotonEstado();
                }))
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> lblEstado.setText("Error: " + RespuestaHttp.mensaje(error)));
                    return null;
                });
    }

    private void editarSeleccionado() {
        Paciente paciente = obtenerSeleccionado();
        if (paciente == null) {
            mostrarAvisoSeleccion();
            return;
        }
        mostrarFormulario(paciente);
    }

    private void mostrarFormulario(Paciente actual) {
        JTextField txtNombre = new JTextField(actual == null ? "" : actual.getNombre(), 24);
        JTextField txtDni = new JTextField(actual == null ? "" : actual.getDni(), 12);
        JTextField txtTelefono = new JTextField(actual == null ? "" : actual.getTelefono(), 12);
        JTextField txtEmail = new JTextField(actual == null || actual.getEmail() == null ? "" : actual.getEmail(), 24);

        JPanel formulario = new JPanel(new GridBagLayout());
        agregarCampo(formulario, 0, "Nombre completo:", txtNombre);
        agregarCampo(formulario, 1, "DNI:", txtDni);
        agregarCampo(formulario, 2, "Teléfono:", txtTelefono);
        agregarCampo(formulario, 3, "Correo:", txtEmail);

        while (true) {
            String titulo = actual == null ? "Nuevo paciente" : "Editar paciente";
            int opcion = JOptionPane.showConfirmDialog(this, formulario, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            String error = validarFormulario(txtNombre, txtDni, txtTelefono, txtEmail);
            if (error != null) {
                Validador.mostrarError(this, error);
                continue;
            }

            Paciente paciente = new Paciente(
                    actual == null ? null : actual.getId(),
                    txtNombre.getText().trim(),
                    txtDni.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim(),
                    actual == null || actual.isActivo()
            );
            guardarPaciente(paciente);
            return;
        }
    }

    private String validarFormulario(JTextField nombre, JTextField dni, JTextField telefono, JTextField email) {
        if (nombre.getText().trim().isEmpty()) {
            return "Ingresa el nombre completo.";
        }
        if (!Validador.validarDNI(dni.getText().trim())) {
            return "El DNI debe contener exactamente 8 dígitos.";
        }
        if (!Validador.validarTelefono(telefono.getText().trim())) {
            return "El teléfono debe contener 9 dígitos y comenzar con 9.";
        }
        if (!email.getText().trim().isEmpty() && !Validador.validarEmail(email.getText().trim())) {
            return "El correo electrónico no tiene un formato válido.";
        }
        return null;
    }

    private void guardarPaciente(Paciente paciente) {
        lblEstado.setText("Guardando paciente...");
        gestor.guardar(paciente)
                .thenAccept(guardado -> SwingUtilities.invokeLater(() -> {
                    Validador.mostrarExito(this, "Paciente guardado correctamente.");
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
        Paciente paciente = obtenerSeleccionado();
        if (paciente == null) {
            mostrarAvisoSeleccion();
            return;
        }
        boolean nuevoEstado = !paciente.isActivo();
        String accion = nuevoEstado ? "reactivar" : "desactivar";
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Deseas " + accion + " a " + paciente.getNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        gestor.cambiarActivo(paciente.getId(), nuevoEstado)
                .thenAccept(guardado -> SwingUtilities.invokeLater(this::cargarTabla))
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> Validador.mostrarError(this, RespuestaHttp.mensaje(error)));
                    return null;
                });
    }

    private Paciente obtenerSeleccionado() {
        int filaVista = tablaPacientes.getSelectedRow();
        if (filaVista < 0) {
            return null;
        }
        int filaModelo = tablaPacientes.convertRowIndexToModel(filaVista);
        return filaModelo < pacientes.size() ? pacientes.get(filaModelo) : null;
    }

    private void actualizarBotonEstado() {
        Paciente paciente = obtenerSeleccionado();
        btnCambiarEstado.setText(paciente != null && !paciente.isActivo() ? "Reactivar" : "Desactivar");
    }

    private void mostrarAvisoSeleccion() {
        JOptionPane.showMessageDialog(this, "Selecciona un paciente primero.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private static void agregarCampo(JPanel panel, int fila, String etiqueta, JTextField campo) {
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
