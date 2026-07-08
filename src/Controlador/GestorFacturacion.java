package Controlador;

import DAO.FacturaDAO;
import DAO.FacturaDAOImpl;
import Decorator.AnalisisSangreDecorator;
import Decorator.CitaBase;
import Decorator.Facturable;
import Decorator.RadiografiaDecorator;
import Modelo.Factura;
import Modelo.Paciente;

import java.sql.SQLException;

public class GestorFacturacion {

    private final FacturaDAO facturaDAO = new FacturaDAOImpl();
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

    public String generarBoleta(Facturable factura, Paciente paciente) {
        String datosPaciente = (paciente != null)
                ? String.format("Paciente: %s\nDNI:      %s\nTelefono: %s\n\n",
                paciente.getNombre(), paciente.getDni(), paciente.getTelefono())
                : "Paciente: (no especificado)\n\n";

        return String.format(
                "========================================\n" +
                        "         CLINICA SAN RAFAEL\n" +
                        "========================================\n" +
                        "%s" +
                        "Detalle:\n%s\n\n" +
                        "----------------------------------------\n" +
                        "TOTAL:  S/ %.2f\n" +
                        "========================================",
                datosPaciente,
                factura.getDescripcion().replace(" + ", "\n  + "),
                factura.getCosto()
        );
    }
    public Factura guardarFactura(Facturable factura, Integer citaId, Paciente paciente) {
        Factura f = (paciente != null)
                ? new Factura(citaId, paciente.getId(), paciente.getNombre(), paciente.getDni(),
                factura.getDescripcion(), factura.getCosto())
                : new Factura(citaId, factura.getDescripcion(), factura.getCosto());
        try {
            facturaDAO.insertar(f);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la factura en la base de datos: " + e.getMessage(), e);
        }
        return f;
    }
}
