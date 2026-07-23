package Controlador;

import DAO.CitaDAO;
import DAO.FacturaDAO;
import DAOImpl.CitaDAOImpl;
import DAOImpl.FacturaDAOImpl;
import Decorator.AnalisisSangreDecorator;
import Decorator.CitaBase;
import Decorator.Facturable;
import Decorator.RadiografiaDecorator;
import Modelo.Cita;
import Modelo.Factura;
import Modelo.Paciente;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class GestorFacturacion {
    private static volatile GestorFacturacion instancia;

    private final FacturaDAO facturaDAO;
    private final CitaDAO citaDAO;

    private GestorFacturacion() {
        this(new FacturaDAOImpl(), new CitaDAOImpl());
    }

    GestorFacturacion(FacturaDAO facturaDAO, CitaDAO citaDAO) {
        this.facturaDAO = facturaDAO;
        this.citaDAO = citaDAO;
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

    public Facturable calcularFactura(
            String motivo,
            boolean incluyeRadiografia,
            boolean incluyeAnalisis
    ) {
        Facturable factura = new CitaBase(motivo);

        if (incluyeRadiografia) {
            factura = new RadiografiaDecorator(factura);
        }

        if (incluyeAnalisis) {
            factura = new AnalisisSangreDecorator(factura);
        }

        return factura;
    }

    public String generarBoleta(
            Facturable facturable,
            Paciente paciente,
            Cita cita
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("        CLÍNICA SAN RAFAEL           \n");
        sb.append("        BOLETA DE ATENCIÓN           \n");
        sb.append("=====================================\n");

        if (paciente != null) {
            sb.append("Paciente: ")
                    .append(paciente.getNombre())
                    .append("\n");
            sb.append("DNI: ")
                    .append(paciente.getDni())
                    .append("\n");
        } else {
            sb.append("Paciente: Público en General\n");
        }

        sb.append("Médico: ")
                .append(obtenerNombreMedico(cita))
                .append("\n");

        sb.append("-------------------------------------\n");
        sb.append("Detalle de los servicios:\n\n");
        sb.append(facturable.getDescripcion()).append("\n");
        sb.append("-------------------------------------\n");
        sb.append(
                String.format(
                        "TOTAL A PAGAR:       S/ %.2f\n",
                        facturable.getCosto()
                )
        );
        sb.append("=====================================\n");

        return sb.toString();
    }

    private static String obtenerNombreMedico(Cita cita) {
        if (cita == null
                || cita.getMedico() == null
                || cita.getMedico().isBlank()) {
            return "No asignado";
        }

        return cita.getMedico().trim();
    }

    public CompletableFuture<Cita> buscarCitaPendienteFacturacion(Long pacienteId) {
        if (pacienteId == null) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<List<Cita>> citasFuture = citaDAO.listarTodos();
        CompletableFuture<List<Factura>> facturasFuture = facturaDAO.listarTodos();

        return citasFuture.thenCombine(facturasFuture, (citas, facturas) ->
                citas.stream()
                        .filter(Objects::nonNull)
                        .filter(cita -> cita.getId() != null)
                        .filter(cita -> cita.getEstado() == Cita.Estado.ATENDIDO)
                        .filter(cita -> cita.getPaciente() != null)
                        .filter(cita -> Objects.equals(
                                cita.getPaciente().getId(),
                                pacienteId
                        ))
                        .filter(cita -> facturas.stream().noneMatch(factura ->
                                factura != null
                                        && Objects.equals(
                                        factura.getCitaId(),
                                        cita.getId()
                                )
                        ))
                        .max(Comparator.comparing(
                                Cita::getFechaHora,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                        .orElse(null)
        );
    }

    public CompletableFuture<List<Factura>> getTodas() {
        return facturaDAO.listarTodos();
    }

    public CompletableFuture<Factura> guardarFactura(
            Facturable facturable,
            Long citaId,
            Paciente paciente
    ) {
        if (facturable == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Primero debes calcular la factura.")
            );
        }

        if (citaId == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("La factura debe estar asociada a una cita atendida.")
            );
        }

        if (paciente == null || paciente.getId() == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Debes seleccionar un paciente válido.")
            );
        }

        return facturaDAO.listarTodos().thenCompose(facturas -> {
            boolean yaFacturada = facturas.stream().anyMatch(factura ->
                    factura != null
                            && Objects.equals(factura.getCitaId(), citaId)
            );

            if (yaFacturada) {
                throw new CompletionException(
                        new IllegalStateException(
                                "La cita seleccionada ya cuenta con una factura registrada."
                        )
                );
            }

            Factura factura = new Factura(
                    citaId,
                    paciente.getId(),
                    paciente.getNombre(),
                    paciente.getDni(),
                    facturable.getDescripcion(),
                    facturable.getCosto()
            );

            return facturaDAO.guardar(factura);
        });
    }
}