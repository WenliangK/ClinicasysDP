package ui.styles;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class TableStyles {

    private TableStyles() {
    }

    public static void aplicarEstilo(JTable tabla) {

        tabla.setFont(UIStyles.NORMAL);
        tabla.setForeground(UIStyles.TEXT);
        tabla.setBackground(UIStyles.CARD);

        tabla.setRowHeight(38);

        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(UIStyles.BORDER);

        tabla.setSelectionBackground(
                new Color(219, 234, 254)
        );

        tabla.setSelectionForeground(
                UIStyles.TEXT
        );

        tabla.setIntercellSpacing(
                new Dimension(0, 1)
        );

        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);

        JTableHeader encabezado =
                tabla.getTableHeader();

        encabezado.setFont(UIStyles.BUTTON);
        encabezado.setForeground(UIStyles.TEXT);

        encabezado.setBackground(
                new Color(248, 250, 252)
        );

        encabezado.setPreferredSize(
                new Dimension(
                        encabezado.getPreferredSize().width,
                        42
                )
        );

        encabezado.setReorderingAllowed(false);

        encabezado.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        UIStyles.BORDER
                )
        );
    }
}

