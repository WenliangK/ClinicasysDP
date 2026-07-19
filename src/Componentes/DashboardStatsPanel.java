package Componentes;

import javax.swing.*;
import java.awt.*;

public class DashboardStatsPanel extends JPanel {

    public DashboardStatsPanel() {

        setOpaque(false);

        setLayout(
                new GridLayout(
                        1,
                        4,
                        18,
                        0
                )
        );
    }

    public void agregarStatCard(
            StatCard card
    ) {
        add(card);
    }

    public void limpiar() {
        removeAll();
        revalidate();
        repaint();
    }
}