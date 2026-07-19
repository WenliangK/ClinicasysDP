package Componentes;


import ui.styles.UIStyles;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Dimension;

public class ModernComboBox<E> extends JComboBox<E> {

    public ModernComboBox() {
        super();
        configurarAspecto();
    }

    public ModernComboBox(E[] elementos) {
        super(elementos);
        configurarAspecto();
    }

    private void configurarAspecto() {

        setFont(UIStyles.NORMAL);
        setForeground(UIStyles.TEXT);
        setBackground(Color.WHITE);

        setPreferredSize(
                new Dimension(
                        getPreferredSize().width,
                        40
                )
        );

        setBorder(
                BorderFactory.createLineBorder(
                        UIStyles.BORDER
                )
        );

        putClientProperty("JComponent.arc", 12);
    }
}