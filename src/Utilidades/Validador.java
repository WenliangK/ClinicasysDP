package Utilidades;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDate;

/**
 * Clase utilitaria centralizada para validaciones.
 * Sigue el principio SRP: una sola clase responsable de todas las validaciones.
 */
public class Validador {

    /** Valida DNI peruano: exactamente 8 digitos numericos. */
    public static boolean validarDNI(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    /** Valida correo electronico con expresion regular. */
    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /** Valida que el telefono tenga 9 digitos (movil peruano). */
    public static boolean validarTelefono(String tel) {
        return tel != null && tel.matches("9\\d{8}");
    }

    /** Valida que ninguno de los campos este vacio. */
    public static boolean validarCamposVacios(JTextField... campos) {
        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) return false;
        }
        return true;
    }

    /** Valida que la fecha no sea en el pasado. */
    public static boolean validarFechaFutura(LocalDate fecha) {
        return fecha != null && !fecha.isBefore(LocalDate.now());
    }

    /**
     * Pinta el borde de un campo de rojo si el valor es invalido,
     * verde si es valido. Llama esto en el evento FocusLost del campo.
     */
    public static void marcarCampo(JTextField campo, boolean valido) {
        Border borde = valido
                ? BorderFactory.createLineBorder(new Color(0, 160, 0), 1)
                : BorderFactory.createLineBorder(new Color(200, 0, 0), 1);
        campo.setBorder(borde);
    }

    /** Muestra un dialogo de error amigable. */
    public static void mostrarError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error de validacion",
                JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un dialogo de exito. */
    public static void mostrarExito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operacion exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }
}