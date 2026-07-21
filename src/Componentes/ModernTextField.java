package Componentes;

import ui.styles.UIStyles;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;

public class ModernTextField extends JTextField {

    public ModernTextField() {
        this(20);
    }

    public ModernTextField(int columnas) {
        super(columnas);
        configurarAspecto();
    }

    public ModernTextField(String texto, int columnas) {
        super(texto, columnas);
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
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyles.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        putClientProperty("JTextField.placeholderText", "");
        putClientProperty("JComponent.arc", 12);
    }

    public void setPlaceholder(String texto) {
        putClientProperty("JTextField.placeholderText", texto);
    }
}