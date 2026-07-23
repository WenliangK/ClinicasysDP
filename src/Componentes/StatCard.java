package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatCard extends RoundedPanel {

    private final JLabel lblTitulo;
    private final JLabel lblValor;
    private final JLabel lblDescripcion;
    private final JLabel lblIcono;

    private Color colorAcento;

    public StatCard(
            String titulo,
            String valor,
            String descripcion
    ) {
        this(
                titulo,
                valor,
                descripcion,
                UIStyles.PRIMARY,
                "●"
        );
    }

    public StatCard(
            String titulo,
            String valor,
            String descripcion,
            Color colorAcento,
            String icono
    ) {
        super(
                new BorderLayout(
                        14,
                        0
                )
        );

        this.colorAcento =
                colorAcento == null
                        ? UIStyles.PRIMARY
                        : colorAcento;

        setBackground(
                UIStyles.CARD_BACKGROUND
        );

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                4,
                                0,
                                0,
                                this.colorAcento
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                16,
                                18,
                                18
                        )
                )
        );

        setPreferredSize(
                new Dimension(
                        210,
                        122
                )
        );

        setMinimumSize(
                new Dimension(
                        170,
                        112
                )
        );


        JPanel panelIcono =
                crearPanelIcono(
                        icono
                );

        lblIcono =
                (JLabel) panelIcono
                        .getComponent(0);


        JPanel panelContenido =
                new JPanel();

        panelContenido.setOpaque(false);

        panelContenido.setLayout(
                new BoxLayout(
                        panelContenido,
                        BoxLayout.Y_AXIS
                )
        );

        lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                UIStyles.SMALL_BOLD
        );

        lblTitulo.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        lblTitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        lblValor =
                new JLabel(
                        valor
                );

        lblValor.setFont(
                UIStyles.STAT_VALUE
        );

        lblValor.setForeground(
                UIStyles.TEXT
        );

        lblValor.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        lblDescripcion =
                new JLabel(
                        descripcion
                );

        lblDescripcion.setFont(
                UIStyles.SMALL
        );

        lblDescripcion.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        lblDescripcion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelContenido.add(
                lblTitulo
        );

        panelContenido.add(
                Box.createVerticalStrut(
                        7
                )
        );

        panelContenido.add(
                lblValor
        );

        panelContenido.add(
                Box.createVerticalStrut(
                        5
                )
        );

        panelContenido.add(
                lblDescripcion
        );

        add(
                panelIcono,
                BorderLayout.WEST
        );

        add(
                panelContenido,
                BorderLayout.CENTER
        );

        configurarHover();
    }

    private JPanel crearPanelIcono(
            String icono
    ) {
        JPanel contenedor =
                new JPanel(
                        new GridBagLayout()
                );

        contenedor.setOpaque(false);

        JLabel etiquetaIcono =
                new JLabel(
                        icono == null
                                ? "●"
                                : icono
                );

        etiquetaIcono.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        etiquetaIcono.setVerticalAlignment(
                SwingConstants.CENTER
        );

        etiquetaIcono.setForeground(
                colorAcento
        );

        etiquetaIcono.setBackground(
                crearColorSuave(
                        colorAcento
                )
        );

        etiquetaIcono.setOpaque(true);

        etiquetaIcono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        20
                )
        );

        etiquetaIcono.setPreferredSize(
                new Dimension(
                        46,
                        46
                )
        );

        etiquetaIcono.setMinimumSize(
                new Dimension(
                        46,
                        46
                )
        );

        etiquetaIcono.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        contenedor.add(
                etiquetaIcono
        );

        return contenedor;
    }

    private Color crearColorSuave(
            Color color
    ) {
        int rojo =
                (color.getRed() + 255) / 2;

        int verde =
                (color.getGreen() + 255) / 2;

        int azul =
                (color.getBlue() + 255) / 2;

        return new Color(
                Math.min(rojo + 35, 255),
                Math.min(verde + 35, 255),
                Math.min(azul + 35, 255)
        );
    }

    private void configurarHover() {
        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {
                        setBackground(
                                UIStyles.HOVER_BACKGROUND
                        );

                        setCursor(
                                Cursor.getPredefinedCursor(
                                        Cursor.HAND_CURSOR
                                )
                        );

                        repaint();
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {
                        setBackground(
                                UIStyles.CARD_BACKGROUND
                        );

                        setCursor(
                                Cursor.getDefaultCursor()
                        );

                        repaint();
                    }
                }
        );
    }

    public void setValor(
            String valor
    ) {
        lblValor.setText(
                valor
        );
    }

    public void setDescripcion(
            String descripcion
    ) {
        lblDescripcion.setText(
                descripcion
        );
    }

    public void setTitulo(
            String titulo
    ) {
        lblTitulo.setText(
                titulo
        );
    }

    public void setIcono(
            String icono
    ) {
        lblIcono.setText(
                icono
        );
    }

    public void setColorAcento(
            Color colorAcento
    ) {
        if (colorAcento == null) {
            return;
        }

        this.colorAcento =
                colorAcento;

        lblIcono.setForeground(
                colorAcento
        );

        lblIcono.setBackground(
                crearColorSuave(
                        colorAcento
                )
        );

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                4,
                                0,
                                0,
                                colorAcento
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                16,
                                18,
                                18
                        )
                )
        );

        repaint();
    }
}