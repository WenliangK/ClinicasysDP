package Controlador;

public class GestorFacturacion {

    /**
     * Calcula el total de una cita base con los examenes seleccionados.
     * @param descripcionCita  descripcion de la consulta
     * @param conRadiografia   si se incluye radiografia (+S/ 30)
     * @param conAnalisisSangre si se incluye analisis de sangre (+S/ 20)
     * @return objeto Facturable con el costo y descripcion acumulados
     */
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

    /** Genera un texto de boleta para mostrar en pantalla. */
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
