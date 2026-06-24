package Vista;

import Controlador.GestorFacturacion;
import Decorator.Facturable;

import javax.swing.*;
import java.awt.*;

public class FacturacionPanel extends JPanel {

    private JTextField txtMotivo;
    private JCheckBox chkRadiografia, chkAnalisisSangre;
    private JTextArea txtResultado;

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

        txtMotivo = new JTextField(25);
        chkRadiografia    = new JCheckBox("Radiografia  (+S/ 30.00)");
        chkAnalisisSangre = new JCheckBox("Analisis de Sangre  (+S/ 20.00)");

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Motivo de consulta:"), gbc);
        gbc.gridx = 1; form.add(txtMotivo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        form.add(new JLabel("Examenes adicionales (Decorator):"), gbc);
        gbc.gridy = 2; form.add(chkRadiografia, gbc);
        gbc.gridy = 3; form.add(chkAnalisisSangre, gbc);

        JButton btnCalcular = new JButton("Calcular y Generar Boleta");
        btnCalcular.addActionListener(e -> calcular());
        gbc.gridy = 4; gbc.gridwidth = 2; form.add(btnCalcular, gbc);

        add(form, BorderLayout.WEST);

        txtResultado = new JTextArea(12, 35);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResultado.setEditable(false);
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);
    }

    private void calcular() {
        try {
            if (txtMotivo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa el motivo de la consulta.",
                        "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            GestorFacturacion gestor = new GestorFacturacion();
            Facturable factura = gestor.calcularFactura(
                    txtMotivo.getText().trim(),
                    chkRadiografia.isSelected(),
                    chkAnalisisSangre.isSelected()
            );
            txtResultado.setText(gestor.generarBoleta(factura));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al calcular: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
