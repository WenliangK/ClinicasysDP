package Componentes;
import ui.styles.UIStyles;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class RoundedPanel extends JPanel {

    private int radio = 18;

    public RoundedPanel() {
        configurarPanel();
    }

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        configurarPanel();
    }

    private void configurarPanel() {
        setOpaque(false);
        setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );
    }

    public void setRadio(int radio) {
        this.radio = radio;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(UIStyles.CARD);

        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                radio,
                radio
        );

        g2.setColor(UIStyles.BORDER);

        g2.drawRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 1,
                radio,
                radio
        );

        g2.dispose();

        super.paintComponent(g);
    }
}