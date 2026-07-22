package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarButton extends JButton {

    private boolean activo;
    private boolean hover;

    public SidebarButton(String texto) {
        this(texto, null);
    }

    public SidebarButton(String texto, Icon icono) {
        super(texto, icono);
        configurarAspecto();
        configurarHover();
    }

    private void configurarAspecto() {
        setFont(UIStyles.BUTTON);
        setForeground(UIStyles.SIDEBAR_TEXT);
        setHorizontalAlignment(SwingConstants.LEFT);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);
        setIconTextGap(14);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setPreferredSize(new Dimension(230, 50));
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 18));
        putClientProperty("JButton.buttonType", "roundRect");
        putClientProperty("JComponent.arc", 16);
    }

    private void configurarHover() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        repaint();
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();
        int arco = 16;

        if (activo) {
            GradientPaint degradado = new GradientPaint(
                    0,
                    0,
                    UIStyles.SIDEBAR_ACTIVE,
                    ancho,
                    0,
                    UIStyles.PRIMARY
            );

            g2.setPaint(degradado);
            g2.fillRoundRect(0, 0, ancho, alto, arco, arco);

            g2.setColor(UIStyles.PRIMARY_LIGHT);
            g2.fillRoundRect(0, 0, 5, alto, 5, 5);

            setForeground(Color.WHITE);
        } else if (hover) {
            g2.setColor(UIStyles.SIDEBAR_HOVER);
            g2.fillRoundRect(0, 0, ancho, alto, arco, arco);
            setForeground(Color.WHITE);
        } else {
            setForeground(UIStyles.SIDEBAR_TEXT);
        }

        g2.dispose();
        super.paintComponent(graphics);
    }
}
