package Vista;

import Controlador.GestorMedicos;
import Modelo.Medico;
import Utilidades.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicosPanel extends JPanel {

    private JTextField txtNombre, txtEspecialidad;
    private JComboBox<String> cbTipo;
    private JTable tablaMedicos;
    private DefaultTableModel modeloTabla;

    private int idSeleccionado = 0;

    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;

    public MedicosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Gestion de Medicos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar / Editar medico"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtNombre       = new JTextField(20);
        txtEspecialidad = new JTextField(20);
        cbTipo          = new JComboBox<>(new String[]{"PRIVADO", "PUBLICO"});

        agregarFila(formulario, gbc, 0, "Nombre completo:", txtNombre);
        agregarFila(formulario, gbc, 1, "Especialidad:", txtEspecialidad);
        agregarFila(formulario, gbc, 2, "Tipo:", cbTipo);

        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar   = new JButton("Eliminar");
        btnLimpiar    = new JButton("Limpiar");
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnRegistrar.addActionListener(e -> registrarMedico());
        btnActualizar.addActionListener(e -> actualizarMedico());
        btnEliminar.addActionListener(e -> eliminarMedico());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotonesForm.add(btnRegistrar);
        panelBotonesForm.add(btnActualizar);
        panelBotonesForm.add(btnEliminar);
        panelBotonesForm.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formulario.add(panelBotonesForm, gbc);

        add(formulario, BorderLayout.WEST);

        String[] columnas = {"ID", "Nombre", "Especialidad", "Tipo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMedicos = new JTable(modeloTabla);
        tablaMedicos.setRowHeight(26);
        tablaMedicos.getTableHeader().setReorderingAllowed(false);
        tablaMedicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccionEnFormulario();
        });
        add(new JScrollPane(tablaMedicos), BorderLayout.CENTER);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String label, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private void registrarMedico() {
        try {
            if (!camposValidos()) return;
            GestorMedicos.getInstancia().registrar(
                    txtNombre.getText().trim(),
                    txtEspecialidad.getText().trim(),
                    (String) cbTipo.getSelectedItem()
            );
            Validador.mostrarExito(this, "Medico registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarMedico() {
        try {
            if (idSeleccionado == 0) {
                Validador.mostrarError(this, "Selecciona un medico de la tabla para editarlo.");
                return;
            }
            if (!camposValidos()) return;
            GestorMedicos.getInstancia().actualizar(
                    idSeleccionado,
                    txtNombre.getText().trim(),
                    txtEspecialidad.getText().trim(),
                    (String) cbTipo.getSelectedItem()
            );
            Validador.mostrarExito(this, "Medico actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMedico() {
        if (idSeleccionado == 0) {
            Validador.mostrarError(this, "Selecciona un medico de la tabla para eliminarlo.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas eliminar a " + txtNombre.getText() + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            GestorMedicos.getInstancia().eliminar(idSeleccionado);
            Validador.mostrarExito(this, "Medico eliminado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean camposValidos() {
        if (!Validador.validarCamposVacios(txtNombre, txtEspecialidad)) {
            Validador.mostrarError(this, "Todos los campos son obligatorios.");
            return false;
        }
        return true;
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tablaMedicos.getSelectedRow();
        if (fila == -1) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        txtEspecialidad.setText((String) modeloTabla.getValueAt(fila, 2));
        cbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 3));
        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void limpiarFormulario() {
        idSeleccionado = 0;
        txtNombre.setText("");
        txtEspecialidad.setText("");
        cbTipo.setSelectedIndex(0);
        tablaMedicos.clearSelection();
        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Medico> medicos = GestorMedicos.getInstancia().getTodos();
        for (Medico m : medicos) {
            modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad(), m.getTipo()});
        }
    }
}