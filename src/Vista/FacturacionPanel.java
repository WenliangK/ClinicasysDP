package Vista;

import Controlador.GestorFacturacion;
import Controlador.GestorPacientes;
import Decorator.Facturable;
import Modelo.Factura;
import Modelo.Paciente;
import Singleton.GestorConfiguracion;
import Utilidades.GeneradorFacturaImagen;
import Utilidades.RespuestaHttp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FacturacionPanel extends JPanel {

    private final GestorFacturacion gestor = GestorFacturacion.getInstancia();

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
        // [El diseño gráfico se mantiene idéntico, he resumido la inicialización para que no haya cambios]
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

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; form.add(new JLabel("Examenes adicionales:"), gbc);
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
        GestorPacientes.getInstancia().getTodos().thenAccept(pacientes -> SwingUtilities.invokeLater(() -> {
            comboPaciente.removeAllItems();
            comboPaciente.addItem(null);
            for (Paciente p : pacientes) { comboPaciente.addItem(p); }
            comboPaciente.setRenderer(new DefaultListCellRendererPaciente());
        })).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error de red: " + RespuestaHttp.mensaje(ex)));
            return null;
        });
    }

    private Paciente pacienteSeleccionado() { return (Paciente) comboPaciente.getSelectedItem(); }

    private void calcular() {
        if (txtMotivo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el motivo.", "Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        facturaCalculada = gestor.calcularFactura(txtMotivo.getText().trim(), chkRadiografia.isSelected(), chkAnalisisSangre.isSelected());
        txtResultado.setText(gestor.generarBoleta(facturaCalculada, pacienteSeleccionado()));
        btnGuardar.setEnabled(true);
        facturaGuardada = null;
        btnDescargarImagen.setEnabled(false);
    }

    private void guardar() {
        if (facturaCalculada == null) return;
        btnGuardar.setEnabled(false);

        gestor.guardarFactura(facturaCalculada, null, pacienteSeleccionado())
                .thenAccept(f -> SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Factura guardada correctamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    facturaGuardada = f;
                    btnDescargarImagen.setEnabled(true);
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Error de red: " + RespuestaHttp.mensaje(ex), "Error", JOptionPane.ERROR_MESSAGE);
                        btnGuardar.setEnabled(true);
                    });
                    return null;
                });
    }

    // El método descargarImagen() y construirItems() se mantienen idénticos (no usan BD)
    private void descargarImagen() {
        if (facturaGuardada == null) return;
        try {
            Paciente paciente = pacienteSeleccionado();
            List<GeneradorFacturaImagen.ItemFactura> items = construirItems();
            BufferedImage imagen = GeneradorFacturaImagen.generar(GestorConfiguracion.getInstancia().getNombreClinica(),
                    facturaGuardada.getId(), facturaGuardada.getFechaEmision(),
                    paciente != null ? paciente.getNombre() : null, paciente != null ? paciente.getDni() : null,
                    paciente != null ? paciente.getTelefono() : null, txtMotivo.getText().trim(), items);

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("factura_" + facturaGuardada.getId() + ".png"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File destino = chooser.getSelectedFile();
                if (!destino.getName().toLowerCase().endsWith(".png")) destino = new File(destino.getParentFile(), destino.getName() + ".png");
                ImageIO.write(imagen, "png", destino);
                JOptionPane.showMessageDialog(this, "Guardada en:\n" + destino.getAbsolutePath(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error generar imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<GeneradorFacturaImagen.ItemFactura> construirItems() {
        List<GeneradorFacturaImagen.ItemFactura> items = new ArrayList<>();
        items.add(new GeneradorFacturaImagen.ItemFactura(1, "Consulta medica: " + txtMotivo.getText().trim(), 50.00));
        if (chkRadiografia.isSelected()) items.add(new GeneradorFacturaImagen.ItemFactura(1, "Radiografia", 30.00));
        if (chkAnalisisSangre.isSelected()) items.add(new GeneradorFacturaImagen.ItemFactura(1, "Analisis de Sangre", 20.00));
        return items;
    }

    private static class DefaultListCellRendererPaciente extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String texto = (value == null) ? "(sin paciente)" : value.toString();
            return super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
        }
    }
}
