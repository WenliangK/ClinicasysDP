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

/** Panel de registro y listado de pacientes con validacion en tiempo real. */
public class PacientesPanel extends JPanel {

    private JTextField txtNombre, txtDni, txtTelefono, txtEmail;
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;

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

        // Formulario de registro
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar nuevo paciente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre   = new JTextField(20);
        txtDni      = new JTextField(10);
        txtTelefono = new JTextField(12);
        txtEmail    = new JTextField(20);

        // Validacion en FocusLost
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

        JButton btnRegistrar = new JButton("Registrar Paciente");
        btnRegistrar.addActionListener(e -> registrarPaciente());
        gbc.gridx = 1; gbc.gridy = 4;
        formulario.add(btnRegistrar, gbc);

        add(formulario, BorderLayout.WEST);

        // Tabla de pacientes
        String[] columnas = {"ID", "Nombre", "DNI", "Telefono", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setRowHeight(26);
        add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String label, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private void registrarPaciente() {
        try {
            if (!Validador.validarCamposVacios(txtNombre, txtDni, txtTelefono, txtEmail)) {
                Validador.mostrarError(this, "Todos los campos son obligatorios.");
                return;
            }
            if (!Validador.validarDNI(txtDni.getText())) {
                Validador.mostrarError(this, "El DNI debe tener exactamente 8 digitos numericos.");
                return;
            }
            if (!Validador.validarTelefono(txtTelefono.getText())) {
                Validador.mostrarError(this, "El telefono debe iniciar con 9 y tener 9 digitos.");
                return;
            }
            if (!Validador.validarEmail(txtEmail.getText())) {
                Validador.mostrarError(this, "El correo electronico no tiene un formato valido.");
                return;
            }
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

    private void limpiarFormulario() {
        txtNombre.setText(""); txtDni.setText("");
        txtTelefono.setText(""); txtEmail.setText("");
    }

    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Paciente p : GestorPacientes.getInstancia().getTodos()) {
            modeloTabla.addRow(new Object[]{p.getId(), p.getNombre(), p.getDni(),
                    p.getTelefono(), p.getEmail()});
        }
    }
}
