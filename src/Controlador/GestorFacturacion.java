package Controlador;

import DAO.FacturaDAO; // <-- Ruta de la interfaz corregida
import DAOImpl.FacturaDAOImpl;
import Decorator.AnalisisSangreDecorator;
import Decorator.CitaBase;
import Decorator.Facturable;
import Decorator.RadiografiaDecorator;
import Modelo.Factura;
import Modelo.Paciente;

import java.util.concurrent.CompletableFuture;

public class GestorFacturacion {

    private static GestorFacturacion instancia;
    private final FacturaDAO facturaDAO = new FacturaDAOImpl();

    public GestorFacturacion() {}

    public static GestorFacturacion getInstancia() {
        if (instancia == null) {
            instancia = new GestorFacturacion();
        }
        return instancia;
    }

    public Facturable calcularFactura(String motivo, boolean incluyeRadiografia, boolean incluyeAnalisis) {
        // Le pasamos la variable "motivo" para que tu CitaBase ya no marque error
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

    public CompletableFuture<Factura> guardarFactura(Facturable facturable, Object extra, Paciente paciente) {
        // 1. Extraemos los datos del paciente de forma segura si no es nulo
        Integer pacienteId = null;
        String pacienteNombre = null;
        String pacienteDni = null;

        if (paciente != null) {
            pacienteId = paciente.getId();
            pacienteNombre = paciente.getNombre();
            pacienteDni = paciente.getDni();
        }

        // 2. Extraemos el ID de la cita (si viene en el objeto 'extra')
        Integer citaId = null;
        if (extra instanceof Integer) {
            citaId = (Integer) extra;
        }

        // 3. ¡AQUÍ ESTÁ LA MAGIA! Usamos tu constructor en lugar de los métodos "set"
        Factura nuevaFactura = new Factura(
                citaId,
                pacienteId,
                pacienteNombre,
                pacienteDni,
                facturable.getDescripcion(),
                facturable.getCosto()
        );

        // 4. Enviamos al servidor
        return facturaDAO.guardar(nuevaFactura);
    }
}