package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusBadge extends JLabel {

    private String estado;

    private Color colorTexto;
    private Color colorFondo;
    private Color colorBorde;

    private int radio = 16;

    /*
     * =========================================================
     * CONSTRUCTOR
     * =========================================================
     */

    public StatusBadge(
            String estado
    ) {
        super(
                formatearTexto(
                        estado
                )
        );

        this.estado =
                estado == null
                        ? ""
                        : estado;

        configurarBadge();
        aplicarEstiloEstado(this.estado);
    }

    /*
     * =========================================================
     * CONFIGURACIÓN GENERAL
     * =========================================================
     */

    private void configurarBadge() {
        setFont(
                UIStyles.SMALL_BOLD
        );

        setHorizontalAlignment(
                SwingConstants.CENTER
        );

        setVerticalAlignment(
                SwingConstants.CENTER
        );

        setOpaque(false);

        setBorder(
                new EmptyBorder(
                        7,
                        12,
                        7,
                        12
                )
        );
    }

    /*
     * =========================================================
     * ESTILO SEGÚN ESTADO
     * =========================================================
     */

    public final void aplicarEstiloEstado(
            String nuevoEstado
    ) {
        estado =
                nuevoEstado == null
                        ? ""
                        : nuevoEstado;

        String estadoNormalizado =
                estado
                        .trim()
                        .toUpperCase();

        switch (estadoNormalizado) {

            case "PROGRAMADA":
                colorTexto =
                        oscurecer(
                                UIStyles.CITAS_ACCENT,
                                20
                        );

                colorFondo =
                        UIStyles.CITAS_ACCENT_LIGHT;

                colorBorde =
                        UIStyles.CITAS_ACCENT;

                break;

            case "EN_CONSULTORIO":
                colorTexto =
                        oscurecer(
                                UIStyles.WARNING,
                                40
                        );

                colorFondo =
                        UIStyles.WARNING_LIGHT;

                colorBorde =
                        UIStyles.WARNING;

                break;

            case "ATENDIDO":
                colorTexto =
                        oscurecer(
                                UIStyles.SUCCESS,
                                35
                        );

                colorFondo =
                        UIStyles.SUCCESS_LIGHT;

                colorBorde =
                        UIStyles.SUCCESS;

                break;

            case "CANCELADO":
                colorTexto =
                        oscurecer(
                                UIStyles.DANGER,
                                25
                        );

                colorFondo =
                        UIStyles.DANGER_LIGHT;

                colorBorde =
                        UIStyles.DANGER;

                break;

            case "ACTIVO":
                colorTexto =
                        oscurecer(
                                UIStyles.SUCCESS,
                                35
                        );

                colorFondo =
                        UIStyles.SUCCESS_LIGHT;

                colorBorde =
                        UIStyles.SUCCESS;

                break;

            case "INACTIVO":
            case "DESACTIVADO":
                colorTexto =
                        UIStyles.TEXT_SECONDARY;

                colorFondo =
                        UIStyles.SOFT_BACKGROUND;

                colorBorde =
                        UIStyles.BORDER_DARK;

                break;

            case "PENDIENTE":
                colorTexto =
                        oscurecer(
                                UIStyles.WARNING,
                                40
                        );

                colorFondo =
                        UIStyles.WARNING_LIGHT;

                colorBorde =
                        UIStyles.WARNING;

                break;

            default:
                colorTexto =
                        UIStyles.TEXT_SECONDARY;

                colorFondo =
                        UIStyles.SOFT_BACKGROUND;

                colorBorde =
                        UIStyles.BORDER_DARK;

                break;
        }

        setText(
                formatearTexto(
                        estado
                )
        );

        setForeground(
                colorTexto
        );

        repaint();
    }

    /*
     * =========================================================
     * PINTADO PERSONALIZADO
     * =========================================================
     */

    @Override
    protected void paintComponent(
            Graphics graphics
    ) {
        Graphics2D g2 =
                (Graphics2D) graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho =
                getWidth();

        int alto =
                getHeight();

        /*
         * Fondo.
         */
        g2.setColor(
                colorFondo == null
                        ? UIStyles.SOFT_BACKGROUND
                        : colorFondo
        );

        g2.fillRoundRect(
                0,
                0,
                Math.max(
                        0,
                        ancho - 1
                ),
                Math.max(
                        0,
                        alto - 1
                ),
                radio,
                radio
        );

        /*
         * Borde.
         */
        g2.setColor(
                colorBorde == null
                        ? UIStyles.BORDER_DARK
                        : colorBorde
        );

        g2.setStroke(
                new BasicStroke(
                        1.1f
                )
        );

        g2.drawRoundRect(
                0,
                0,
                Math.max(
                        0,
                        ancho - 2
                ),
                Math.max(
                        0,
                        alto - 2
                ),
                radio,
                radio
        );

        g2.dispose();

        super.paintComponent(
                graphics
        );
    }

    /*
     * =========================================================
     * MÉTODOS PÚBLICOS
     * =========================================================
     */

    public void setEstado(
            String nuevoEstado
    ) {
        aplicarEstiloEstado(
                nuevoEstado
        );
    }

    public String getEstado() {
        return estado;
    }

    public void setRadio(
            int radio
    ) {
        this.radio =
                Math.max(
                        0,
                        radio
                );

        repaint();
    }

    public int getRadio() {
        return radio;
    }

    /*
     * =========================================================
     * UTILIDADES
     * =========================================================
     */

    private static String formatearTexto(
            String texto
    ) {
        if (texto == null || texto.isBlank()) {
            return "SIN ESTADO";
        }

        return texto
                .trim()
                .replace(
                        "_",
                        " "
                )
                .toUpperCase();
    }

    private Color oscurecer(
            Color color,
            int cantidad
    ) {
        if (color == null) {
            return UIStyles.TEXT_SECONDARY;
        }

        return new Color(
                Math.max(
                        0,
                        color.getRed() - cantidad
                ),
                Math.max(
                        0,
                        color.getGreen() - cantidad
                ),
                Math.max(
                        0,
                        color.getBlue() - cantidad
                )
        );
    }
}