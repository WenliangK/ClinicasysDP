package Utilidades;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDate;

public class Validador {

    public static boolean validarDNI(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean validarTelefono(String tel) {
        return tel != null && tel.matches("9\\d{8}");
    }

    public static boolean validarCamposVacios(JTextField... campos) {
        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) return false;
        }
        return true;
    }
    public static boolean validarFechaFutura(LocalDate fecha) {
        return fecha != null && !fecha.isBefore(LocalDate.now());
    }

    public static void marcarCampo(JTextField campo, boolean valido) {
        Border borde = valido
                ? BorderFactory.createLineBorder(new Color(0, 160, 0), 1)
                : BorderFactory.createLineBorder(new Color(200, 0, 0), 1);
        campo.setBorder(borde);
    }

    public static void mostrarError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error de validacion",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarExito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operacion exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }
}