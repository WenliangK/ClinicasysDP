package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CitasChartPanel extends JPanel {

    private int programadas;
    private int enConsultorio;

    public CitasChartPanel() {
        setOpaque(false);

        setPreferredSize(
                new Dimension(0, 190)
        );

        setMinimumSize(
                new Dimension(300, 170)
        );
    }

    public void actualizarDatos(
            int programadas,
            int enConsultorio
    ) {
        this.programadas = Math.max(programadas, 0);
        this.enConsultorio = Math.max(enConsultorio, 0);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 =
                (Graphics2D) graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        int margenIzquierdo = 55;
        int margenDerecho = 25;
        int margenSuperior = 28;
        int margenInferior = 42;

        int anchoGrafico =
                Math.max(
                        1,
                        getWidth()
                                - margenIzquierdo
                                - margenDerecho
                );

        int altoGrafico =
                Math.max(
                        1,
                        getHeight()
                                - margenSuperior
                                - margenInferior
                );

        int mayorValor =
                Math.max(
                        programadas,
                        enConsultorio
                );

        /*
         * Una escala mínima de 4 evita que el eje muestre
         * repetidamente el número 1.
         */
        int valorMaximo =
                Math.max(
                        4,
                        redondearEscala(mayorValor)
                );

        dibujarLineasGuia(
                g2,
                margenIzquierdo,
                margenSuperior,
                anchoGrafico,
                altoGrafico,
                valorMaximo
        );

        int anchoBarra =
                Math.min(
                        100,
                        Math.max(
                                55,
                                anchoGrafico / 8
                        )
                );

        int separacion =
                Math.max(
                        100,
                        anchoGrafico / 5
                );

        int centro =
                margenIzquierdo
                        + anchoGrafico / 2;

        int xProgramadas =
                centro
                        - separacion
                        - anchoBarra / 2;

        int xConsultorio =
                centro
                        + separacion
                        - anchoBarra / 2;

        dibujarBarra(
                g2,
                xProgramadas,
                margenSuperior,
                altoGrafico,
                anchoBarra,
                programadas,
                valorMaximo,
                new Color(53, 103, 246),
                new Color(224, 232, 255),
                "Programadas"
        );

        dibujarBarra(
                g2,
                xConsultorio,
                margenSuperior,
                altoGrafico,
                anchoBarra,
                enConsultorio,
                valorMaximo,
                new Color(240, 166, 48),
                new Color(255, 239, 210),
                "En consultorio"
        );

        g2.dispose();
    }

    private int redondearEscala(int valor) {
        if (valor <= 4) {
            return 4;
        }

        int resto = valor % 4;

        return resto == 0
                ? valor
                : valor + (4 - resto);
    }

    private void dibujarLineasGuia(
            Graphics2D g2,
            int x,
            int y,
            int ancho,
            int alto,
            int valorMaximo
    ) {
        int divisiones = 4;

        g2.setFont(UIStyles.SMALL);

        for (int i = 0; i <= divisiones; i++) {
            int posicionY =
                    y
                            + alto
                            - alto * i / divisiones;

            int valor =
                    valorMaximo
                            * i
                            / divisiones;

            g2.setColor(
                    new Color(225, 229, 235)
            );

            g2.drawLine(
                    x,
                    posicionY,
                    x + ancho,
                    posicionY
            );

            String texto =
                    String.valueOf(valor);

            FontMetrics metricas =
                    g2.getFontMetrics();

            g2.setColor(
                    UIStyles.TEXT_SECONDARY
            );

            g2.drawString(
                    texto,
                    x
                            - metricas.stringWidth(texto)
                            - 10,
                    posicionY + 4
            );
        }
    }

    private void dibujarBarra(
            Graphics2D g2,
            int x,
            int y,
            int altoGrafico,
            int anchoBarra,
            int valor,
            int valorMaximo,
            Color colorPrincipal,
            Color colorVacio,
            String etiqueta
    ) {
        /*
         * Fondo de referencia.
         * Permite visualizar el gráfico aunque el valor sea cero.
         */
        g2.setColor(colorVacio);

        RoundRectangle2D fondo =
                new RoundRectangle2D.Double(
                        x,
                        y,
                        anchoBarra,
                        altoGrafico,
                        14,
                        14
                );

        g2.fill(fondo);

        int altoBarra =
                valor == 0
                        ? 0
                        : Math.max(
                        8,
                        (int) (
                                altoGrafico
                                        * valor
                                        / (double) valorMaximo
                        )
                );

        int posicionY =
                y
                        + altoGrafico
                        - altoBarra;

        if (altoBarra > 0) {
            g2.setColor(colorPrincipal);

            RoundRectangle2D barra =
                    new RoundRectangle2D.Double(
                            x,
                            posicionY,
                            anchoBarra,
                            altoBarra,
                            14,
                            14
                    );

            g2.fill(barra);
        }

        String textoValor =
                String.valueOf(valor);

        g2.setFont(UIStyles.BUTTON);
        g2.setColor(UIStyles.TEXT);

        FontMetrics metricasValor =
                g2.getFontMetrics();

        int yValor =
                valor == 0
                        ? y + altoGrafico - 12
                        : Math.max(
                        y + 18,
                        posicionY - 8
                );

        g2.drawString(
                textoValor,
                x
                        + (
                        anchoBarra
                                - metricasValor.stringWidth(textoValor)
                ) / 2,
                yValor
        );

        g2.setFont(UIStyles.SMALL);
        g2.setColor(UIStyles.TEXT_SECONDARY);

        FontMetrics metricasEtiqueta =
                g2.getFontMetrics();

        g2.drawString(
                etiqueta,
                x
                        + (
                        anchoBarra
                                - metricasEtiqueta.stringWidth(etiqueta)
                ) / 2,
                y
                        + altoGrafico
                        + 26
        );
    }
}