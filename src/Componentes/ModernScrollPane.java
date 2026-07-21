package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollPane extends JScrollPane {

    public ModernScrollPane(
            Component componente
    ) {
        super(componente);

        configurarScrollPane();
        configurarBarras();
    }

    private void configurarScrollPane() {
        setBorder(
                BorderFactory.createLineBorder(
                        UIStyles.BORDER,
                        1,
                        true
                )
        );

        setBackground(
                UIStyles.CARD_BACKGROUND
        );

        setOpaque(
                true
        );

        getViewport().setOpaque(
                true
        );

        getViewport().setBackground(
                UIStyles.CARD_BACKGROUND
        );

        setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        getVerticalScrollBar().setUnitIncrement(
                16
        );

        getHorizontalScrollBar().setUnitIncrement(
                16
        );

        setViewportBorder(
                BorderFactory.createEmptyBorder()
        );
    }

    private void configurarBarras() {
        JScrollBar barraVertical =
                getVerticalScrollBar();

        JScrollBar barraHorizontal =
                getHorizontalScrollBar();

        barraVertical.setPreferredSize(
                new Dimension(
                        10,
                        0
                )
        );

        barraHorizontal.setPreferredSize(
                new Dimension(
                        0,
                        10
                )
        );

        barraVertical.setUI(
                crearScrollBarUI()
        );

        barraHorizontal.setUI(
                crearScrollBarUI()
        );
    }

    private BasicScrollBarUI crearScrollBarUI() {
        return new BasicScrollBarUI() {

            @Override
            protected void configureScrollBarColors() {
                thumbColor =
                        UIStyles.BORDER_DARK;

                trackColor =
                        UIStyles.SOFT_BACKGROUND;
            }

            @Override
            protected JButton createDecreaseButton(
                    int orientation
            ) {
                return crearBotonInvisible();
            }

            @Override
            protected JButton createIncreaseButton(
                    int orientation
            ) {
                return crearBotonInvisible();
            }

            @Override
            protected void paintThumb(
                    Graphics g,
                    JComponent componente,
                    Rectangle limites
            ) {
                if (!componente.isEnabled()
                        || limites.width <= 0
                        || limites.height <= 0) {
                    return;
                }

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(
                        thumbColor
                );

                g2.fillRoundRect(
                        limites.x + 2,
                        limites.y + 2,
                        Math.max(
                                0,
                                limites.width - 4
                        ),
                        Math.max(
                                0,
                                limites.height - 4
                        ),
                        8,
                        8
                );

                g2.dispose();
            }

            @Override
            protected void paintTrack(
                    Graphics g,
                    JComponent componente,
                    Rectangle limites
            ) {
                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setColor(
                        trackColor
                );

                g2.fillRect(
                        limites.x,
                        limites.y,
                        limites.width,
                        limites.height
                );

                g2.dispose();
            }
        };
    }

    private JButton crearBotonInvisible() {
        JButton boton =
                new JButton();

        boton.setPreferredSize(
                new Dimension(
                        0,
                        0
                )
        );

        boton.setMinimumSize(
                new Dimension(
                        0,
                        0
                )
        );

        boton.setMaximumSize(
                new Dimension(
                        0,
                        0
                )
        );

        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusable(false);

        return boton;
    }
}