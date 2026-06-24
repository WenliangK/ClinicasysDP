package Controlador;

import Decorator.AnalisisSangreDecorator;
import Decorator.CitaBase;
import Decorator.Facturable;
import Decorator.RadiografiaDecorator;

public class GestorFacturacion {
    public Facturable calcularFactura(String descripcionCita,
                                      boolean conRadiografia,
                                      boolean conAnalisisSangre) {
        Facturable factura = new CitaBase(descripcionCita);

        if (conRadiografia) {
            factura = new RadiografiaDecorator(factura);
        }
        if (conAnalisisSangre) {
            factura = new AnalisisSangreDecorator(factura);
        }

        return factura;
    }

    public String generarBoleta(Facturable factura) {
        return String.format(
                "========================================\n" +
                        "         CLINICA SAN RAFAEL\n" +
                        "========================================\n" +
                        "Detalle:\n%s\n\n" +
                        "----------------------------------------\n" +
                        "TOTAL:  S/ %.2f\n" +
                        "========================================",
                factura.getDescripcion().replace(" + ", "\n  + "),
                factura.getCosto()
        );
    }
}
