package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {

    public enum Tipo {
        PRIMARIO,
        SECUNDARIO,
        EXITO,
        ADVERTENCIA,
        PELIGRO
    }

    private Tipo tipo;

    private Color colorBase;
    private Color colorHover;
    private Color colorPressed;
    private Color colorTexto;

    private boolean mouseEncima;
    private boolean presionado;

    public ModernButton(
            String texto,
            Tipo tipo
    ) {
        super(texto);

        this.tipo =
                tipo == null
                        ? Tipo.PRIMARIO
                        : tipo;

        configurarBoton();
        aplicarTipo(this.tipo);
        configurarEventosVisuales();
    }

    public ModernButton(
            String texto
    ) {
        this(
                texto,
                Tipo.PRIMARIO
        );
    }



    private void configurarBoton() {
        setFont(
                UIStyles.BUTTON
        );

        setForeground(
                Color.WHITE
        );

        setFocusPainted(
                false
        );

        setBorderPainted(
                false
        );

        setContentAreaFilled(
                false
        );

        setOpaque(
                false
        );

        setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        setHorizontalAlignment(
                SwingConstants.CENTER
        );

        setVerticalAlignment(
                SwingConstants.CENTER
        );

        setIconTextGap(
                9
        );

        setBorder(
                new EmptyBorder(
                        11,
                        18,
                        11,
                        18
                )
        );

        setPreferredSize(
                new Dimension(
                        170,
                        42
                )
        );

        setMinimumSize(
                new Dimension(
                        120,
                        40
                )
        );
    }



    public final void aplicarTipo(
            Tipo nuevoTipo
    ) {
        if (nuevoTipo == null) {
            nuevoTipo = Tipo.PRIMARIO;
        }

        tipo =
                nuevoTipo;

        switch (tipo) {

            case SECUNDARIO:
                colorBase =
                        UIStyles.CARD_BACKGROUND;

                colorHover =
                        UIStyles.HOVER_BACKGROUND;

                colorPressed =
                        UIStyles.PRIMARY_LIGHT;

                colorTexto =
                        UIStyles.TEXT;

                break;

            case EXITO:
                colorBase =
                        UIStyles.SUCCESS;

                colorHover =
                        oscurecerColor(
                                UIStyles.SUCCESS,
                                15
                        );

                colorPressed =
                        oscurecerColor(
                                UIStyles.SUCCESS,
                                28
                        );

                colorTexto =
                        Color.WHITE;

                break;

            case ADVERTENCIA:
                colorBase =
                        UIStyles.WARNING;

                colorHover =
                        oscurecerColor(
                                UIStyles.WARNING,
                                14
                        );

                colorPressed =
                        oscurecerColor(
                                UIStyles.WARNING,
                                26
                        );

                colorTexto =
                        Color.WHITE;

                break;

            case PELIGRO:
                colorBase =
                        UIStyles.DANGER;

                colorHover =
                        oscurecerColor(
                                UIStyles.DANGER,
                                14
                        );

                colorPressed =
                        oscurecerColor(
                                UIStyles.DANGER,
                                26
                        );

                colorTexto =
                        Color.WHITE;

                break;

            case PRIMARIO:
            default:
                colorBase =
                        UIStyles.PRIMARY;

                colorHover =
                        UIStyles.PRIMARY_DARK;

                colorPressed =
                        oscurecerColor(
                                UIStyles.PRIMARY_DARK,
                                18
                        );

                colorTexto =
                        Color.WHITE;

                break;
        }

        setForeground(
                colorTexto
        );

        repaint();
    }



    private void configurarEventosVisuales() {
        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {
                        if (!isEnabled()) {
                            return;
                        }

                        mouseEncima =
                                true;

                        repaint();
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {
                        mouseEncima =
                                false;

                        presionado =
                                false;

                        repaint();
                    }

                    @Override
                    public void mousePressed(
                            MouseEvent e
                    ) {
                        if (!isEnabled()) {
                            return;
                        }

                        presionado =
                                true;

                        repaint();
                    }

                    @Override
                    public void mouseReleased(
                            MouseEvent e
                    ) {
                        presionado =
                                false;

                        repaint();
                    }
                }
        );
    }



    @Override
    protected void paintComponent(
            Graphics g
    ) {
        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        Color fondo =
                obtenerColorActual();


        if (isEnabled()
                && tipo != Tipo.SECUNDARIO) {

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            24
                    )
            );

            g2.fillRoundRect(
                    1,
                    3,
                    getWidth() - 2,
                    getHeight() - 4,
                    14,
                    14
            );
        }


        g2.setColor(
                fondo
        );

        g2.fillRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 3,
                14,
                14
        );


        if (tipo == Tipo.SECUNDARIO) {
            g2.setColor(
                    mouseEncima
                            ? UIStyles.PRIMARY
                            : UIStyles.BORDER_DARK
            );

            g2.setStroke(
                    new BasicStroke(
                            1.2f
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 2,
                    getHeight() - 4,
                    14,
                    14
            );
        }

        g2.dispose();

        super.paintComponent(
                g
        );
    }

    private Color obtenerColorActual() {
        if (!isEnabled()) {
            return new Color(
                    203,
                    208,
                    218
            );
        }

        if (presionado) {
            return colorPressed;
        }

        if (mouseEncima) {
            return colorHover;
        }

        return colorBase;
    }

    private Color oscurecerColor(
            Color color,
            int cantidad
    ) {
        if (color == null) {
            return UIStyles.PRIMARY_DARK;
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



    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(
            Tipo tipo
    ) {
        aplicarTipo(
                tipo
        );
    }

    public void setTexto(
            String texto
    ) {
        setText(
                texto == null
                        ? ""
                        : texto
        );
    }
}