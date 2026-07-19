package Componentes;

import ui.styles.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarButton extends JButton {

    private boolean activo;

    public SidebarButton(String texto) {
        super(texto);

        configurarAspecto();
        configurarHover();
    }

    private void configurarAspecto() {
        setFont(UIStyles.BUTTON);
        setForeground(UIStyles.SIDEBAR_TEXT);
        setBackground(UIStyles.SIDEBAR);

        setHorizontalAlignment(SwingConstants.LEFT);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        setPreferredSize(new Dimension(220, 48));

        setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        18,
                        0,
                        18
                )
        );

        putClientProperty("JButton.buttonType", "roundRect");
        putClientProperty("JComponent.arc", 16);
    }

    private void configurarHover() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!activo) {
                    setBackground(UIStyles.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                actualizarColor();
            }
        });
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        actualizarColor();
    }

    public boolean isActivo() {
        return activo;
    }

    private void actualizarColor() {
        if (activo) {
            setBackground(UIStyles.SIDEBAR_ACTIVE);
            setForeground(Color.WHITE);
        } else {
            setBackground(UIStyles.SIDEBAR);
            setForeground(UIStyles.SIDEBAR_TEXT);
        }
    }
}
