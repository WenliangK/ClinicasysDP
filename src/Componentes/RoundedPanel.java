package Componentes;



import Vista.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RoundedPanel extends JPanel {

    public RoundedPanel() {

        setBackground(theme.CARD);

        setBorder(new EmptyBorder(20,20,20,20));

    }

}
