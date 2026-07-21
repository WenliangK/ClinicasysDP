package Vista;

import Componentes.DashboardCard;
import Componentes.ModernButton;
import Componentes.ModernComboBox;
import Componentes.ModernScrollPane;
import Componentes.ModernTextField;
import Componentes.RoundedPanel;
import Componentes.SectionHeader;
import Componentes.StatusBadge;
import Controlador.GestorFacturacion;
import Controlador.GestorPacientes;
import Decorator.Facturable;
import Modelo.Factura;
import Modelo.Paciente;
import Singleton.GestorConfiguracion;
import Utilidades.GeneradorFacturaImagen;
import Utilidades.RespuestaHttp;
import ui.styles.UIStyles;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FacturacionPanel extends JPanel {

    private final GestorFacturacion gestor =
            GestorFacturacion.getInstancia();

    private ModernComboBox<Paciente> comboPaciente;
    private ModernTextField txtMotivo;

    private JCheckBox chkRadiografia;
    private JCheckBox chkAnalisisSangre;

    private JTextArea txtResultado;

    private ModernButton btnCalcular;
    private ModernButton btnGuardar;
    private ModernButton btnDescargarImagen;

    private JLabel lblEstado;

    private Facturable facturaCalculada;
    private Factura facturaGuardada;

    public FacturacionPanel() {
        configurarPanel();
        inicializarComponentes();
        cargarPacientes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    private void inicializarComponentes() {
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(
                new BoxLayout(contenido, BoxLayout.Y_AXIS)
        );
        contenido.setBorder(
                BorderFactory.createEmptyBorder(20, 24, 24, 24)
        );

        SectionHeader encabezado = crearEncabezado();
        JPanel contenidoPrincipal = crearContenidoPrincipal();

        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenidoPrincipal.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        encabezado.getPreferredSize().height
                )
        );

        contenidoPrincipal.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)
        );

        contenido.add(encabezado);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(contenidoPrincipal);

        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.setOpaque(false);
        scrollPrincipal.getViewport().setOpaque(false);
        scrollPrincipal.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPrincipal.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPrincipal, BorderLayout.CENTER);
    }

    private SectionHeader crearEncabezado() {
        SectionHeader encabezado = new SectionHeader(
                "Módulo de facturación",
                "Calcula, registra y descarga las boletas de atención médica.",
                UIStyles.PRIMARY,
                "▤"
        );

        encabezado.setRightComponent(
                new StatusBadge("FACTURACIÓN")
        );

        return encabezado;
    }

    private JPanel crearContenidoPrincipal() {
        JPanel contenido = new JPanel(
                new GridBagLayout()
        );
        contenido.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.weightx = 0.48;
        gbc.insets = new Insets(0, 0, 0, 10);
        contenido.add(crearTarjetaFormulario(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.52;
        gbc.insets = new Insets(0, 10, 0, 0);
        contenido.add(crearTarjetaBoleta(), gbc);

        return contenido;
    }

    private DashboardCard crearTarjetaFormulario() {
        DashboardCard tarjeta = new DashboardCard(
                "Generar boleta",
                "Selecciona el paciente y los servicios realizados.",
                UIStyles.PRIMARY,
                "＋"
        );

        JPanel contenido = new JPanel(
                new BorderLayout(0, 18)
        );
        contenido.setOpaque(false);

        contenido.add(
                crearResumenFormulario(),
                BorderLayout.NORTH
        );
        contenido.add(
                crearFormulario(),
                BorderLayout.CENTER
        );
        contenido.add(
                crearPanelEstado(),
                BorderLayout.SOUTH
        );

        tarjeta.setContenido(contenido);
        tarjeta.setPreferredSize(new Dimension(0, 820));
        tarjeta.setMinimumSize(new Dimension(430, 820));

        return tarjeta;
    }

    private JPanel crearResumenFormulario() {
        JPanel panel = new JPanel(
                new BorderLayout(14, 0)
        );
        panel.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel(
                "Información de la atención"
        );
        titulo.setFont(UIStyles.BUTTON);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = crearEtiquetaAjustable(
                "Completa los datos y calcula el importe antes de guardar.",
                300
        );
        descripcion.setFont(UIStyles.SMALL);
        descripcion.setForeground(UIStyles.TEXT_SECONDARY);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(descripcion);

        panel.add(textos, BorderLayout.CENTER);
        panel.add(
                new StatusBadge("NUEVA BOLETA"),
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(
                new GridBagLayout()
        );
        formulario.setOpaque(false);

        comboPaciente = new ModernComboBox<>();
        comboPaciente.setRenderer(
                new DefaultListCellRendererPaciente()
        );

        txtMotivo = new ModernTextField("", 25);
        txtMotivo.setPlaceholder(
                "Ejemplo: Consulta general"
        );

        chkRadiografia = crearCheckBox(
                "Radiografía",
                "Servicio adicional: S/ 30.00"
        );

        chkAnalisisSangre = crearCheckBox(
                "Análisis de sangre",
                "Servicio adicional: S/ 20.00"
        );

        btnCalcular = new ModernButton(
                "Calcular y generar boleta",
                ModernButton.Tipo.PRIMARIO
        );

        btnGuardar = new ModernButton(
                "Guardar factura",
                ModernButton.Tipo.SECUNDARIO
        );

        btnDescargarImagen = new ModernButton(
                "Descargar como imagen",
                ModernButton.Tipo.SECUNDARIO
        );

        btnGuardar.setEnabled(false);
        btnDescargarImagen.setEnabled(false);

        btnCalcular.addActionListener(e -> calcular());
        btnGuardar.addActionListener(e -> guardar());
        btnDescargarImagen.addActionListener(
                e -> descargarImagen()
        );

        agregarBloqueCampo(
                formulario,
                0,
                "Paciente",
                "Puedes generar la boleta con o sin paciente asociado.",
                comboPaciente
        );

        agregarBloqueCampo(
                formulario,
                1,
                "Motivo de consulta",
                "Describe el servicio médico principal realizado.",
                txtMotivo
        );

        agregarSeccionExamenes(formulario, 2);
        agregarBotones(formulario, 5);

        return formulario;
    }

    private JCheckBox crearCheckBox(
            String texto,
            String descripcion
    ) {
        JCheckBox checkBox = new JCheckBox(texto);
        checkBox.setOpaque(false);
        checkBox.setFont(UIStyles.NORMAL);
        checkBox.setForeground(UIStyles.TEXT);
        checkBox.setToolTipText(descripcion);
        checkBox.setFocusPainted(false);

        return checkBox;
    }

    private void agregarSeccionExamenes(
            JPanel formulario,
            int filaInicial
    ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = filaInicial;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(18, 0, 8, 0);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setOpaque(false);
        tituloPanel.setLayout(
                new BoxLayout(tituloPanel, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel(
                "Servicios adicionales"
        );
        titulo.setFont(UIStyles.BUTTON);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = crearEtiquetaAjustable(
                "Selecciona los exámenes que se incluirán en la boleta.",
                330
        );
        descripcion.setFont(UIStyles.SMALL);
        descripcion.setForeground(UIStyles.TEXT_SECONDARY);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        tituloPanel.add(titulo);
        tituloPanel.add(Box.createVerticalStrut(3));
        tituloPanel.add(descripcion);

        formulario.add(tituloPanel, gbc);

        gbc.gridy = filaInicial + 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        formulario.add(
                crearOpcionServicio(
                        chkRadiografia,
                        "S/ 30.00"
                ),
                gbc
        );

        gbc.gridy = filaInicial + 2;
        formulario.add(
                crearOpcionServicio(
                        chkAnalisisSangre,
                        "S/ 20.00"
                ),
                gbc
        );
    }

    private JPanel crearOpcionServicio(
            JCheckBox checkBox,
            String precio
    ) {
        RoundedPanel opcion = new RoundedPanel(
                new BorderLayout(12, 0)
        );

        opcion.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyles.BORDER
                        ),
                        new EmptyBorder(12, 14, 12, 14)
                )
        );

        JLabel lblPrecio = new JLabel(precio);
        lblPrecio.setFont(UIStyles.BUTTON);
        lblPrecio.setForeground(UIStyles.PRIMARY);

        opcion.setPreferredSize(
                new Dimension(300, 54)
        );
        opcion.setMinimumSize(
                new Dimension(260, 54)
        );

        opcion.add(checkBox, BorderLayout.CENTER);
        opcion.add(lblPrecio, BorderLayout.EAST);

        return opcion;
    }

    private void agregarBotones(
            JPanel formulario,
            int fila
    ) {
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(
                new BoxLayout(panelBotones, BoxLayout.Y_AXIS)
        );
        panelBotones.setOpaque(false);

        configurarBotonAncho(btnCalcular);
        configurarBotonAncho(btnGuardar);
        configurarBotonAncho(btnDescargarImagen);

        panelBotones.add(btnCalcular);
        panelBotones.add(Box.createVerticalStrut(9));
        panelBotones.add(btnGuardar);
        panelBotones.add(Box.createVerticalStrut(9));
        panelBotones.add(btnDescargarImagen);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 8, 0);

        formulario.add(panelBotones, gbc);
    }

    private static void configurarBotonAncho(
            ModernButton boton
    ) {
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );
        boton.setPreferredSize(
                new Dimension(260, 44)
        );
        boton.setMinimumSize(
                new Dimension(220, 44)
        );
    }

    private JPanel crearPanelEstado() {
        JPanel panelEstado = new JPanel(
                new BorderLayout()
        );
        panelEstado.setOpaque(false);

        panelEstado.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                UIStyles.BORDER
                        ),
                        new EmptyBorder(16, 0, 0, 0)
                )
        );

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel(
                "Estado de la operación"
        );
        titulo.setFont(UIStyles.SMALL);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEstado = new JLabel(
                "Completa los datos para generar la boleta."
        );
        lblEstado.setFont(UIStyles.SMALL);
        lblEstado.setForeground(UIStyles.TEXT_SECONDARY);
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(lblEstado);

        panelEstado.add(textos, BorderLayout.WEST);

        return panelEstado;
    }

    private DashboardCard crearTarjetaBoleta() {
        DashboardCard tarjeta = new DashboardCard(
                "Vista previa de la boleta",
                "Revisa el detalle y los importes antes de guardar.",
                UIStyles.PRIMARY,
                "▧"
        );

        JPanel contenido = new JPanel(
                new BorderLayout(0, 16)
        );
        contenido.setOpaque(false);

        contenido.add(
                crearResumenBoleta(),
                BorderLayout.NORTH
        );
        contenido.add(
                crearVistaPrevia(),
                BorderLayout.CENTER
        );

        tarjeta.setContenido(contenido);
        tarjeta.setPreferredSize(new Dimension(0, 820));
        tarjeta.setMinimumSize(new Dimension(480, 820));

        return tarjeta;
    }

    private JPanel crearResumenBoleta() {
        JPanel panel = new JPanel(
                new BorderLayout(12, 0)
        );
        panel.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel(
                "Documento generado"
        );
        titulo.setFont(UIStyles.BUTTON);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = crearEtiquetaAjustable(
                "El resumen de la factura aparecerá en esta sección.",
                330
        );
        descripcion.setFont(UIStyles.SMALL);
        descripcion.setForeground(UIStyles.TEXT_SECONDARY);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(descripcion);

        panel.add(textos, BorderLayout.CENTER);
        panel.add(
                new StatusBadge("VISTA PREVIA"),
                BorderLayout.EAST
        );

        return panel;
    }

    private ModernScrollPane crearVistaPrevia() {
        txtResultado = new JTextArea(14, 35);

        txtResultado.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        txtResultado.setEditable(false);
        txtResultado.setLineWrap(false);
        txtResultado.setBackground(Color.WHITE);
        txtResultado.setForeground(UIStyles.TEXT);
        txtResultado.setCaretColor(UIStyles.TEXT);

        txtResultado.setBorder(
                new EmptyBorder(18, 18, 18, 18)
        );

        txtResultado.setText(
                "La vista previa de la boleta aparecerá aquí."
        );

        ModernScrollPane scroll = new ModernScrollPane(
                txtResultado
        );
        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyles.BORDER
                )
        );

        return scroll;
    }

    private static void agregarBloqueCampo(
            JPanel panel,
            int fila,
            String tituloTexto,
            String ayudaTexto,
            JComponent campo
    ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(
                fila == 0 ? 0 : 20,
                0,
                0,
                0
        );

        JPanel bloque = new JPanel(
                new BorderLayout(0, 10)
        );
        bloque.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(UIStyles.NORMAL);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ayuda = crearEtiquetaAjustable(
                ayudaTexto,
                330
        );
        ayuda.setFont(UIStyles.SMALL);
        ayuda.setForeground(UIStyles.TEXT_SECONDARY);
        ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(ayuda);

        campo.setPreferredSize(
                new Dimension(240, 44)
        );
        campo.setMinimumSize(
                new Dimension(160, 44)
        );

        bloque.setPreferredSize(
                new Dimension(320, 92)
        );
        bloque.setMinimumSize(
                new Dimension(260, 92)
        );

        bloque.add(textos, BorderLayout.NORTH);
        bloque.add(campo, BorderLayout.CENTER);

        panel.add(bloque, gbc);
    }

    private static JLabel crearEtiquetaAjustable(
            String texto,
            int ancho
    ) {
        JLabel etiqueta = new JLabel(
                "<html><div style='width:"
                        + ancho
                        + "px;'>"
                        + texto
                        + "</div></html>"
        );

        etiqueta.setVerticalAlignment(SwingConstants.TOP);

        return etiqueta;
    }

    public void cargarPacientes() {
        lblEstado.setText(
                "Cargando pacientes..."
        );

        GestorPacientes
                .getInstancia()
                .getTodos()
                .thenAccept(pacientes ->
                        SwingUtilities.invokeLater(() -> {
                            comboPaciente.removeAllItems();
                            comboPaciente.addItem(null);

                            for (Paciente paciente : pacientes) {
                                comboPaciente.addItem(paciente);
                            }

                            comboPaciente.setRenderer(
                                    new DefaultListCellRendererPaciente()
                            );

                            lblEstado.setText(
                                    pacientes.size()
                                            + " paciente(s) disponible(s)."
                            );
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "No se pudieron cargar los pacientes."
                        );

                        JOptionPane.showMessageDialog(
                                this,
                                "Error de red: "
                                        + RespuestaHttp.mensaje(error),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    });

                    return null;
                });
    }

    private Paciente pacienteSeleccionado() {
        return (Paciente) comboPaciente.getSelectedItem();
    }

    private void calcular() {
        String motivo =
                txtMotivo.getText().trim();

        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingresa el motivo.",
                    "Requerido",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        facturaCalculada =
                gestor.calcularFactura(
                        motivo,
                        chkRadiografia.isSelected(),
                        chkAnalisisSangre.isSelected()
                );

        txtResultado.setText(
                gestor.generarBoleta(
                        facturaCalculada,
                        pacienteSeleccionado()
                )
        );

        txtResultado.setCaretPosition(0);

        btnGuardar.setEnabled(true);

        facturaGuardada = null;

        btnDescargarImagen.setEnabled(false);

        lblEstado.setText(
                "Boleta calculada. Ya puedes guardar la factura."
        );
    }

    private void guardar() {
        if (facturaCalculada == null) {
            return;
        }

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        lblEstado.setText(
                "Guardando factura en la base de datos..."
        );

        gestor.guardarFactura(
                        facturaCalculada,
                        null,
                        pacienteSeleccionado()
                )
                .thenAccept(factura ->
                        SwingUtilities.invokeLater(() -> {
                            facturaGuardada = factura;

                            btnGuardar.setText(
                                    "Factura guardada"
                            );

                            btnDescargarImagen.setEnabled(true);

                            lblEstado.setText(
                                    "Factura guardada correctamente."
                            );

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Factura guardada correctamente.",
                                    "Éxito",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(
                                "Guardar factura"
                        );

                        lblEstado.setText(
                                "No se pudo guardar la factura."
                        );

                        JOptionPane.showMessageDialog(
                                this,
                                "Error de red: "
                                        + RespuestaHttp.mensaje(error),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    });

                    return null;
                });
    }

    private void descargarImagen() {
        if (facturaGuardada == null) {
            return;
        }

        try {
            Paciente paciente =
                    pacienteSeleccionado();

            List<GeneradorFacturaImagen.ItemFactura> items =
                    construirItems();

            BufferedImage imagen =
                    GeneradorFacturaImagen.generar(
                            GestorConfiguracion
                                    .getInstancia()
                                    .getNombreClinica(),

                            facturaGuardada.getId(),

                            facturaGuardada
                                    .getFechaEmision(),

                            paciente != null
                                    ? paciente.getNombre()
                                    : null,

                            paciente != null
                                    ? paciente.getDni()
                                    : null,

                            paciente != null
                                    ? paciente.getTelefono()
                                    : null,

                            txtMotivo
                                    .getText()
                                    .trim(),

                            items
                    );

            JFileChooser chooser =
                    new JFileChooser();

            chooser.setDialogTitle(
                    "Guardar factura como imagen"
            );

            chooser.setSelectedFile(
                    new File(
                            "factura_"
                                    + facturaGuardada.getId()
                                    + ".png"
                    )
            );

            int opcion =
                    chooser.showSaveDialog(this);

            if (opcion
                    == JFileChooser.APPROVE_OPTION) {

                File destino =
                        chooser.getSelectedFile();

                if (!destino
                        .getName()
                        .toLowerCase()
                        .endsWith(".png")) {

                    destino =
                            new File(
                                    destino.getParentFile(),
                                    destino.getName() + ".png"
                            );
                }

                ImageIO.write(
                        imagen,
                        "png",
                        destino
                );

                lblEstado.setText(
                        "Factura descargada correctamente."
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Guardada en:\n"
                                + destino.getAbsolutePath(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (IOException error) {
            lblEstado.setText(
                    "No se pudo generar la imagen."
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar la imagen: "
                            + error.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private List<GeneradorFacturaImagen.ItemFactura>
    construirItems() {

        List<GeneradorFacturaImagen.ItemFactura> items =
                new ArrayList<>();

        items.add(
                new GeneradorFacturaImagen.ItemFactura(
                        1,
                        "Consulta médica: "
                                + txtMotivo
                                .getText()
                                .trim(),
                        50.00
                )
        );

        if (chkRadiografia.isSelected()) {
            items.add(
                    new GeneradorFacturaImagen.ItemFactura(
                            1,
                            "Radiografía",
                            30.00
                    )
            );
        }

        if (chkAnalisisSangre.isSelected()) {
            items.add(
                    new GeneradorFacturaImagen.ItemFactura(
                            1,
                            "Análisis de sangre",
                            20.00
                    )
            );
        }

        return items;
    }

    private static class DefaultListCellRendererPaciente
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> lista,
                Object valor,
                int indice,
                boolean seleccionado,
                boolean tieneFoco
        ) {
            String texto =
                    valor == null
                            ? "(sin paciente)"
                            : valor.toString();

            return super.getListCellRendererComponent(
                    lista,
                    texto,
                    indice,
                    seleccionado,
                    tieneFoco
            );
        }
    }
}