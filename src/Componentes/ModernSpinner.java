package Componentes;

import ui.styles.UIStyles;

import javax.swing.BorderFactory;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import java.awt.Color;
import java.awt.Dimension;

public class ModernSpinner extends JSpinner {

    public ModernSpinner() {
        super();
        configurarAspecto();
    }

    public ModernSpinner(SpinnerModel modelo) {
        super(modelo);
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