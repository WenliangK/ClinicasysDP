package Vista;

import Controlador.GestorPacientes;
import Modelo.Paciente;
import Utilidades.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class PacientesPanel extends JPanel {

    private JTextField txtNombre, txtDni, txtTelefono, txtEmail;
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;

    /** ID del paciente actualmente cargado en el formulario para edicion. 0 = ninguno (modo "nuevo"). */
    private int idSeleccionado = 0;

    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;

    public PacientesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Gestion de Pacientes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar / Editar paciente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre   = new JTextField(20);
        txtDni      = new JTextField(10);
        txtTelefono = new JTextField(12);
        txtEmail    = new JTextField(20);

        txtDni.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                Validador.marcarCampo(txtDni, Validador.validarDNI(txtDni.getText()));
            }
        });
        txtTelefono.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                Validador.marcarCampo(txtTelefono, Validador.validarTelefono(txtTelefono.getText()));
            }
        });
        txtEmail.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                Validador.marcarCampo(txtEmail, Validador.validarEmail(txtEmail.getText()));
            }
        });

        agregarFila(formulario, gbc, 0, "Nombre completo:", txtNombre);
        agregarFila(formulario, gbc, 1, "DNI (8 digitos):", txtDni);
        agregarFila(formulario, gbc, 2, "Telefono:", txtTelefono);
        agregarFila(formulario, gbc, 3, "Email:", txtEmail);

        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRegistrar  = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar   = new JButton("Eliminar");
        btnLimpiar    = new JButton("Limpiar");
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnRegistrar.addActionListener(e -> registrarPaciente());
        btnActualizar.addActionListener(e -> actualizarPaciente());
        btnEliminar.addActionListener(e -> eliminarPaciente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotonesForm.add(btnRegistrar);
        panelBotonesForm.add(btnActualizar);
        panelBotonesForm.add(btnEliminar);
        panelBotonesForm.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formulario.add(panelBotonesForm, gbc);

        add(formulario, BorderLayout.WEST);

        String[] columnas = {"ID", "Nombre", "DNI", "Telefono", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setRowHeight(26);
        tablaPacientes.getTableHeader().setReorderingAllowed(false);
        tablaPacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccionEnFormulario();
        });
        add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String label, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private void registrarPaciente() {
        try {
            if (!camposValidos()) return;
            GestorPacientes.getInstancia().registrar(
                    txtNombre.getText().trim(),
                    txtDni.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtEmail.getText().trim()
            );
            Validador.mostrarExito(this, "Paciente registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarPaciente() {
        try {
            if (idSeleccionado == 0) {
                Validador.mostrarError(this, "Selecciona un paciente de la tabla para editarlo.");
                return;
            }
            if (!camposValidos()) return;
            GestorPacientes.getInstancia().actualizar(
                    idSeleccionado,
                    txtNombre.getText().trim(),
                    txtDni.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtEmail.getText().trim()
            );
            Validador.mostrarExito(this, "Paciente actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPaciente() {
        if (idSeleccionado == 0) {
            Validador.mostrarError(this, "Selecciona un paciente de la tabla para eliminarlo.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas eliminar a " + txtNombre.getText() + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            GestorPacientes.getInstancia().eliminar(idSeleccionado);
            Validador.mostrarExito(this, "Paciente eliminado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar (revisa que no tenga citas asociadas): " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean camposValidos() {
        if (!Validador.validarCamposVacios(txtNombre, txtDni, txtTelefono, txtEmail)) {
            Validador.mostrarError(this, "Todos los campos son obligatorios.");
            return false;
        }
        if (!Validador.validarDNI(txtDni.getText())) {
            Validador.mostrarError(this, "El DNI debe tener exactamente 8 digitos numericos.");
            return false;
        }
        if (!Validador.validarTelefono(txtTelefono.getText())) {
            Validador.mostrarError(this, "El telefono debe iniciar con 9 y tener 9 digitos.");
            return false;
        }
        if (!Validador.validarEmail(txtEmail.getText())) {
            Validador.mostrarError(this, "El correo electronico no tiene un formato valido.");
            return false;
        }
        return true;
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila == -1) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        txtDni.setText((String) modeloTabla.getValueAt(fila, 2));
        txtTelefono.setText((String) modeloTabla.getValueAt(fila, 3));
        txtEmail.setText((String) modeloTabla.getValueAt(fila, 4));
        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void limpiarFormulario() {
        idSeleccionado = 0;
        txtNombre.setText(""); txtDni.setText("");
        txtTelefono.setText(""); txtEmail.setText("");
        txtNombre.setBorder(UIManager.getBorder("TextField.border"));
        txtDni.setBorder(UIManager.getBorder("TextField.border"));
        txtTelefono.setBorder(UIManager.getBorder("TextField.border"));
        txtEmail.setBorder(UIManager.getBorder("TextField.border"));
        tablaPacientes.clearSelection();
        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Paciente p : GestorPacientes.getInstancia().getTodos()) {
            modeloTabla.addRow(new Object[]{p.getId(), p.getNombre(), p.getDni(),
                    p.getTelefono(), p.getEmail()});
        }
    }
}