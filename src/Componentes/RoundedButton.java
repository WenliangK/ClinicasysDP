package Componentes;





import Vista.theme;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    public RoundedButton(String text) {
        super(text);

        setFont(theme.NORMAL);
        setBackground(theme.PRIMARY);
        setForeground(Color.WHITE);

        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
    }

}
