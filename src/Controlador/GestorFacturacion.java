package Controlador;

import DAO.FacturaDAO;
import DAOImpl.FacturaDAOImpl;
import Decorator.AnalisisSangreDecorator;
import Decorator.CitaBase;
import Decorator.Facturable;
import Decorator.RadiografiaDecorator;
import Modelo.Factura;
import Modelo.Paciente;

import java.util.concurrent.CompletableFuture;

public final class GestorFacturacion {
    private static volatile GestorFacturacion instancia;
    private final FacturaDAO facturaDAO;

    public GestorFacturacion() {
        this(new FacturaDAOImpl());
    }

    GestorFacturacion(FacturaDAO facturaDAO) {
        this.facturaDAO = facturaDAO;
    }

    public static GestorFacturacion getInstancia() {
        GestorFacturacion actual = instancia;
        if (actual == null) {
            synchronized (GestorFacturacion.class) {
                actual = instancia;
                if (actual == null) {
                    actual = new GestorFacturacion();
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    public Facturable calcularFactura(String motivo, boolean incluyeRadiografia, boolean incluyeAnalisis) {
        Facturable factura = new CitaBase(motivo);
        if (incluyeRadiografia) {
            factura = new RadiografiaDecorator(factura);
        }
        if (incluyeAnalisis) {
            factura = new AnalisisSangreDecorator(factura);
        }
        return factura;
    }

    public String generarBoleta(Facturable facturable, Paciente paciente) {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("        CLÍNICA SAN RAFAEL           \n");
        sb.append("        BOLETA DE ATENCIÓN           \n");
        sb.append("=====================================\n");
        if (paciente != null) {
            sb.append("Paciente: ").append(paciente.getNombre()).append("\n");
            sb.append("DNI: ").append(paciente.getDni()).append("\n");
        } else {
            sb.append("Paciente: Público en General\n");
        }
        sb.append("-------------------------------------\n");
        sb.append("Detalle de los servicios:\n\n");
        sb.append(facturable.getDescripcion()).append("\n");
        sb.append("-------------------------------------\n");
        sb.append(String.format("TOTAL A PAGAR:       S/ %.2f\n", facturable.getCosto()));
        sb.append("=====================================\n");
        return sb.toString();
    }

    public CompletableFuture<Factura> guardarFactura(Facturable facturable, Object citaIdEntrada,
                                                      Paciente paciente) {
        Long pacienteId = paciente == null ? null : paciente.getId();
        String pacienteNombre = paciente == null ? null : paciente.getNombre();
        String pacienteDni = paciente == null ? null : paciente.getDni();
        Long citaId = citaIdEntrada instanceof Number numero ? numero.longValue() : null;

        Factura factura = new Factura(
                citaId,
                pacienteId,
                pacienteNombre,
                pacienteDni,
                facturable.getDescripcion(),
                facturable.getCosto()
        );
        return facturaDAO.guardar(factura);
    }
}