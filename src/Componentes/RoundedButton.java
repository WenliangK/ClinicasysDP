package Componentes;

import javax.swing.*;
import java.awt.*;
import ui.styles.UIStyles;

public class RoundedButton extends JButton {

    public RoundedButton(String text) {
        super(text);

        setFont(UIStyles.NORMAL);
        setBackground(UIStyles.PRIMARY);
        setForeground(Color.WHITE);

        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
    }

}
