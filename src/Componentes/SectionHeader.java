package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SectionHeader extends JPanel {

    private final JLabel lblTitulo;
    private final JLabel lblDescripcion;
    private final JLabel lblIcono;

    private final JPanel panelDerecho;
    private final JPanel barraAcento;

    private Color colorAcento;

    /*
     * =========================================================
     * CONSTRUCTOR SIMPLE
     * =========================================================
     */

    public SectionHeader(
            String titulo,
            String descripcion
    ) {
        this(
                titulo,
                descripcion,
                UIStyles.PRIMARY,
                "◆"
        );
    }

    /*
     * =========================================================
     * CONSTRUCTOR COMPLETO
     * =========================================================
     */

    public SectionHeader(
            String titulo,
            String descripcion,
            Color colorAcento,
            String icono
    ) {
        this.colorAcento =
                colorAcento == null
                        ? UIStyles.PRIMARY
                        : colorAcento;

        configurarPanel();

        barraAcento =
                crearBarraAcento();

        lblIcono =
                crearIcono(
                        icono
                );

        lblTitulo =
                crearTitulo(
                        titulo
                );

        lblDescripcion =
                crearDescripcion(
                        descripcion
                );

        panelDerecho =
                crearPanelDerecho();

        construirContenido();
    }

    /*
     * =========================================================
     * CONFIGURACIÓN PRINCIPAL
     * =========================================================
     */

    private void configurarPanel() {
        setLayout(
                new BorderLayout(
                        16,
                        0
                )
        );

        setOpaque(false);

        setBorder(
                new EmptyBorder(
                        4,
                        0,
                        8,
                        0
                )
        );

        setPreferredSize(
                new Dimension(
                        0,
                        72
                )
        );

        setMinimumSize(
                new Dimension(
                        0,
                        72
                )
        );
    }

    /*
     * =========================================================
     * CONSTRUCCIÓN DEL ENCABEZADO
     * =========================================================
     */

    private void construirContenido() {
        JPanel panelIzquierdo =
                crearPanelIzquierdo();

        JPanel panelTextos =
                crearPanelTextos();

        add(
                panelIzquierdo,
                BorderLayout.WEST
        );

        add(
                panelTextos,
                BorderLayout.CENTER
        );

        add(
                panelDerecho,
                BorderLayout.EAST
        );
    }

    /*
     * =========================================================
     * PANEL IZQUIERDO
     * =========================================================
     */

    private JPanel crearPanelIzquierdo() {
        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        panel.setOpaque(false);

        panel.add(
                barraAcento,
                BorderLayout.WEST
        );

        JPanel contenedorIcono =
                new JPanel(
                        new GridBagLayout()
                );

        contenedorIcono.setOpaque(false);

        contenedorIcono.add(
                lblIcono
        );

        panel.add(
                contenedorIcono,
                BorderLayout.CENTER
        );

        return panel;
    }

    /*
     * =========================================================
     * BARRA DE ACENTO
     * =========================================================
     */

    private JPanel crearBarraAcento() {
        JPanel barra =
                new JPanel();

        barra.setBackground(
                colorAcento
        );

        Dimension dimension =
                new Dimension(
                        5,
                        60
                );

        barra.setPreferredSize(
                dimension
        );

        barra.setMinimumSize(
                dimension
        );

        barra.setMaximumSize(
                dimension
        );

        return barra;
    }

    /*
     * =========================================================
     * ICONO
     * =========================================================
     */

    private JLabel crearIcono(
            String icono
    ) {
        JLabel etiqueta =
                new JLabel(
                        validarIcono(
                                icono
                        ),
                        SwingConstants.CENTER
                );

        etiqueta.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        22
                )
        );

        etiqueta.setForeground(
                colorAcento
        );

        etiqueta.setBackground(
                crearColorSuave(
                        colorAcento
                )
        );

        etiqueta.setOpaque(true);

        Dimension dimension =
                new Dimension(
                        50,
                        50
                );

        etiqueta.setPreferredSize(
                dimension
        );

        etiqueta.setMinimumSize(
                dimension
        );

        etiqueta.setMaximumSize(
                dimension
        );

        etiqueta.setBorder(
                new EmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        return etiqueta;
    }

    /*
     * =========================================================
     * TÍTULO
     * =========================================================
     */

    private JLabel crearTitulo(
            String titulo
    ) {
        JLabel etiqueta =
                new JLabel(
                        titulo == null
                                ? ""
                                : titulo
                );

        etiqueta.setFont(
                UIStyles.TITLE
        );

        etiqueta.setForeground(
                UIStyles.TEXT
        );

        etiqueta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return etiqueta;
    }

    /*
     * =========================================================
     * DESCRIPCIÓN
     * =========================================================
     */

    private JLabel crearDescripcion(
            String descripcion
    ) {
        JLabel etiqueta =
                new JLabel(
                        descripcion == null
                                ? ""
                                : descripcion
                );

        etiqueta.setFont(
                UIStyles.NORMAL
        );

        etiqueta.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        etiqueta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return etiqueta;
    }

    /*
     * =========================================================
     * PANEL DE TEXTOS
     * =========================================================
     */

    private JPanel crearPanelTextos() {
        JPanel panel =
                new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.add(
                Box.createVerticalGlue()
        );

        panel.add(
                lblTitulo
        );

        panel.add(
                Box.createVerticalStrut(
                        5
                )
        );

        panel.add(
                lblDescripcion
        );

        panel.add(
                Box.createVerticalGlue()
        );

        return panel;
    }

    /*
     * =========================================================
     * PANEL DERECHO
     * =========================================================
     */

    private JPanel crearPanelDerecho() {
        JPanel panel =
                new JPanel(
                        new GridBagLayout()
                );

        panel.setOpaque(false);

        panel.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        0,
                        0
                )
        );

        return panel;
    }

    /*
     * =========================================================
     * COMPONENTE DERECHO
     * =========================================================
     */

    public void setRightComponent(
            Component componente
    ) {
        panelDerecho.removeAll();

        if (componente != null) {
            panelDerecho.add(
                    componente
            );
        }

        panelDerecho.revalidate();
        panelDerecho.repaint();

        revalidate();
        repaint();
    }

    public void clearRightComponent() {
        panelDerecho.removeAll();

        panelDerecho.revalidate();
        panelDerecho.repaint();

        revalidate();
        repaint();
    }

    /*
     * =========================================================
     * MÉTODOS DE ACTUALIZACIÓN VISUAL
     * =========================================================
     */

    public void setTitulo(
            String titulo
    ) {
        lblTitulo.setText(
                titulo == null
                        ? ""
                        : titulo
        );

        revalidate();
        repaint();
    }

    public void setDescripcion(
            String descripcion
    ) {
        lblDescripcion.setText(
                descripcion == null
                        ? ""
                        : descripcion
        );

        revalidate();
        repaint();
    }

    public void setIcono(
            String icono
    ) {
        lblIcono.setText(
                validarIcono(
                        icono
                )
        );

        revalidate();
        repaint();
    }

    public void setColorAcento(
            Color nuevoColor
    ) {
        if (nuevoColor == null) {
            return;
        }

        colorAcento =
                nuevoColor;

        barraAcento.setBackground(
                nuevoColor
        );

        lblIcono.setForeground(
                nuevoColor
        );

        lblIcono.setBackground(
                crearColorSuave(
                        nuevoColor
                )
        );

        revalidate();
        repaint();
    }

    /*
     * =========================================================
     * MÉTODOS DE CONSULTA
     * =========================================================
     */

    public String getTitulo() {
        return lblTitulo.getText();
    }

    public String getDescripcion() {
        return lblDescripcion.getText();
    }

    public Color getColorAcento() {
        return colorAcento;
    }

    public JPanel getPanelDerecho() {
        return panelDerecho;
    }

    /*
     * =========================================================
     * UTILIDADES
     * =========================================================
     */

    private String validarIcono(
            String icono
    ) {
        if (icono == null || icono.isBlank()) {
            return "◆";
        }

        return icono;
    }

    private Color crearColorSuave(
            Color color
    ) {
        if (color == null) {
            return UIStyles.PRIMARY_LIGHT;
        }

        int rojo =
                Math.min(
                        255,
                        (color.getRed() + 255) / 2 + 35
                );

        int verde =
                Math.min(
                        255,
                        (color.getGreen() + 255) / 2 + 35
                );

        int azul =
                Math.min(
                        255,
                        (color.getBlue() + 255) / 2 + 35
                );

        return new Color(
                rojo,
                verde,
                azul
        );
    }
}