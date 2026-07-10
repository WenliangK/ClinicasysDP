package Vista;

import Controlador.GestorFacturacion;
import DAO.PacienteDAO;
import DAO.PacienteDAOImpl;
import Decorator.Facturable;
import Modelo.Factura;
import Modelo.Paciente;
import Singleton.GestorConfiguracion;
import Utilidades.GeneradorFacturaImagen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class FacturacionPanel extends JPanel {

    private final GestorFacturacion gestor = new GestorFacturacion();
    private final PacienteDAO pacienteDAO = new PacienteDAOImpl();

    private JComboBox<Paciente> comboPaciente;
    private JTextField txtMotivo;
    private JCheckBox chkRadiografia, chkAnalisisSangre;
    private JTextArea txtResultado;
    private JButton btnGuardar, btnDescargarImagen;
    private Facturable facturaCalculada;
    private Factura facturaGuardada;

    public FacturacionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Modulo de Facturacion");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Generar boleta de atencion"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        comboPaciente = new JComboBox<>();
        cargarPacientes();

        txtMotivo = new JTextField(25);
        chkRadiografia    = new JCheckBox("Radiografia  (+S/ 30.00)");
        chkAnalisisSangre = new JCheckBox("Analisis de Sangre  (+S/ 20.00)");

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; form.add(comboPaciente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Motivo de consulta:"), gbc);
        gbc.gridx = 1; form.add(txtMotivo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        form.add(new JLabel("Examenes adicionales:"), gbc);
        gbc.gridy = 3; form.add(chkRadiografia, gbc);
        gbc.gridy = 4; form.add(chkAnalisisSangre, gbc);

        JButton btnCalcular = new JButton("Calcular y Generar Boleta");
        btnCalcular.addActionListener(e -> calcular());
        gbc.gridy = 5; gbc.gridwidth = 2; form.add(btnCalcular, gbc);

        btnGuardar = new JButton("Guardar Factura en Base de Datos");
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(e -> guardar());
        gbc.gridy = 6; form.add(btnGuardar, gbc);

        btnDescargarImagen = new JButton("Descargar Factura (Imagen)");
        btnDescargarImagen.setEnabled(false);
        btnDescargarImagen.addActionListener(e -> descargarImagen());
        gbc.gridy = 7; form.add(btnDescargarImagen, gbc);

        add(form, BorderLayout.WEST);

        txtResultado = new JTextArea(14, 35);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResultado.setEditable(false);
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);
    }

    public void cargarPacientes() {
        comboPaciente.removeAllItems();
        comboPaciente.addItem(null); // opcion "sin paciente"
        try {
            List<Paciente> pacientes = pacienteDAO.listarTodos();
            for (Paciente p : pacientes) {
                comboPaciente.addItem(p);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de pacientes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        comboPaciente.setRenderer(new DefaultListCellRendererPaciente());
    }

    private Paciente pacienteSeleccionado() {
        return (Paciente) comboPaciente.getSelectedItem();
    }

    private void calcular() {
        try {
            if (txtMotivo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa el motivo de la consulta.",
                        "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            facturaCalculada = gestor.calcularFactura(
                    txtMotivo.getText().trim(),
                    chkRadiografia.isSelected(),
                    chkAnalisisSangre.isSelected()
            );
            txtResultado.setText(gestor.generarBoleta(facturaCalculada, pacienteSeleccionado()));
            btnGuardar.setEnabled(true);
            facturaGuardada = null;
            btnDescargarImagen.setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al calcular: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardar() {
        if (facturaCalculada == null) return;
        try {
            Factura f = gestor.guardarFactura(facturaCalculada, null, pacienteSeleccionado());
            JOptionPane.showMessageDialog(this,
                    "Factura #" + f.getId() + " guardada correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            btnGuardar.setEnabled(false);
            facturaGuardada = f;
            btnDescargarImagen.setEnabled(true);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la factura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void descargarImagen() {
        if (facturaGuardada == null) {
            JOptionPane.showMessageDialog(this, "Primero guarda la factura.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Paciente paciente = pacienteSeleccionado();
            List<GeneradorFacturaImagen.ItemFactura> items = construirItems();

            BufferedImage imagen = GeneradorFacturaImagen.generar(
                    GestorConfiguracion.getInstancia().getNombreClinica(),
                    facturaGuardada.getId(),
                    facturaGuardada.getFechaEmision(),
                    paciente != null ? paciente.getNombre() : null,
                    paciente != null ? paciente.getDni() : null,
                    paciente != null ? paciente.getTelefono() : null,
                    txtMotivo.getText().trim(),
                    items
            );

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("factura_" + facturaGuardada.getId() + ".png"));
            int seleccion = chooser.showSaveDialog(this);
            if (seleccion != JFileChooser.APPROVE_OPTION) return;

            File destino = chooser.getSelectedFile();
            if (!destino.getName().toLowerCase().endsWith(".png")) {
                destino = new File(destino.getParentFile(), destino.getName() + ".png");
            }
            ImageIO.write(imagen, "png", destino);

            JOptionPane.showMessageDialog(this,
                    "Factura guardada como imagen en:\n" + destino.getAbsolutePath(),
                    "Descarga exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<GeneradorFacturaImagen.ItemFactura> construirItems() {
        List<GeneradorFacturaImagen.ItemFactura> items = new ArrayList<>();
        items.add(new GeneradorFacturaImagen.ItemFactura(1, "Consulta medica: " + txtMotivo.getText().trim(), 50.00));
        if (chkRadiografia.isSelected()) {
            items.add(new GeneradorFacturaImagen.ItemFactura(1, "Radiografia", 30.00));
        }
        if (chkAnalisisSangre.isSelected()) {
            items.add(new GeneradorFacturaImagen.ItemFactura(1, "Analisis de Sangre", 20.00));
        }
        return items;
    }

    private static class DefaultListCellRendererPaciente extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            String texto = (value == null) ? "(sin paciente)" : value.toString();
            return super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
        }
    }
}