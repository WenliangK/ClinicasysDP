package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardCard extends RoundedPanel {

    private final JPanel panelContenido;
    private final JLabel lblTitulo;
    private final JLabel lblDescripcion;
    private final JLabel lblIcono;
    private final JPanel barraSuperior;

    private Color colorAcento;

    public DashboardCard(
            String titulo,
            String descripcion
    ) {
        this(
                titulo,
                descripcion,
                UIStyles.PRIMARY,
                "▣"
        );
    }

    public DashboardCard(
            String titulo,
            String descripcion,
            Color colorAcento,
            String icono
    ) {
        super(
                new BorderLayout(
                        0,
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
                BorderFactory.createLineBorder(
                        UIStyles.BORDER
                )
        );

        barraSuperior =
                new JPanel();

        barraSuperior.setBackground(
                this.colorAcento
        );

        barraSuperior.setPreferredSize(
                new Dimension(
                        0,
                        5
                )
        );

        add(
                barraSuperior,
                BorderLayout.NORTH
        );

        /*
         * Contenedor general interior.
         */
        JPanel cuerpo =
                new JPanel(
                        new BorderLayout(
                                0,
                                16
                        )
                );

        cuerpo.setOpaque(false);

        cuerpo.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        20,
                        20,
                        20
                )
        );


        JPanel encabezado =
                new JPanel(
                        new BorderLayout(
                                14,
                                0
                        )
                );

        encabezado.setOpaque(false);


        lblIcono =
                new JLabel(
                        icono == null
                                ? "▣"
                                : icono
                );

        lblIcono.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblIcono.setVerticalAlignment(
                SwingConstants.CENTER
        );

        lblIcono.setForeground(
                this.colorAcento
        );

        lblIcono.setBackground(
                crearColorSuave(
                        this.colorAcento
                )
        );

        lblIcono.setOpaque(true);

        lblIcono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        19
                )
        );

        lblIcono.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );

        lblIcono.setBorder(
                BorderFactory.createEmptyBorder(
                        7,
                        7,
                        7,
                        7
                )
        );


        JPanel panelTextos =
                new JPanel();

        panelTextos.setOpaque(false);

        panelTextos.setLayout(
                new BoxLayout(
                        panelTextos,
                        BoxLayout.Y_AXIS
                )
        );

        lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                UIStyles.SECTION_TITLE
        );

        lblTitulo.setForeground(
                UIStyles.TEXT
        );

        lblTitulo.setAlignmentX(
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

        panelTextos.add(
                lblTitulo
        );

        panelTextos.add(
                Box.createVerticalStrut(
                        4
                )
        );

        panelTextos.add(
                lblDescripcion
        );

        encabezado.add(
                lblIcono,
                BorderLayout.WEST
        );

        encabezado.add(
                panelTextos,
                BorderLayout.CENTER
        );


        JSeparator separador =
                new JSeparator();

        separador.setForeground(
                UIStyles.BORDER
        );

        separador.setBackground(
                UIStyles.BORDER
        );


        JPanel bloqueEncabezado =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        bloqueEncabezado.setOpaque(false);

        bloqueEncabezado.add(
                encabezado,
                BorderLayout.CENTER
        );

        bloqueEncabezado.add(
                separador,
                BorderLayout.SOUTH
        );


        panelContenido =
                new JPanel(
                        new BorderLayout()
                );

        panelContenido.setOpaque(false);

        cuerpo.add(
                bloqueEncabezado,
                BorderLayout.NORTH
        );

        cuerpo.add(
                panelContenido,
                BorderLayout.CENTER
        );

        add(
                cuerpo,
                BorderLayout.CENTER
        );

        configurarHover();
    }

    private Color crearColorSuave(
            Color color
    ) {
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

    private void configurarHover() {
        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {
                        setBackground(
                                new Color(
                                        252,
                                        253,
                                        255
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

                        repaint();
                    }
                }
        );
    }

    public void setContenido(
            Component componente
    ) {
        panelContenido.removeAll();

        if (componente != null) {
            panelContenido.add(
                    componente,
                    BorderLayout.CENTER
            );
        }

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    public JPanel getPanelContenido() {
        return panelContenido;
    }

    public void setTitulo(
            String titulo
    ) {
        lblTitulo.setText(
                titulo
        );
    }

    public void setDescripcion(
            String descripcion
    ) {
        lblDescripcion.setText(
                descripcion
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

        barraSuperior.setBackground(
                colorAcento
        );

        lblIcono.setForeground(
                colorAcento
        );

        lblIcono.setBackground(
                crearColorSuave(
                        colorAcento
                )
        );

        repaint();
    }
}