package Controlador;

import DAO.CitaDAO;
import DAOImpl.CitaDAOImpl;
import Modelo.Cita;
import Modelo.Paciente;
import Observer.Sujeto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class GestorCitas extends Sujeto {
    private static GestorCitas instancia;
    private final CitaDAO citaDAO = new CitaDAOImpl();

    private GestorCitas() {}

    public static synchronized GestorCitas getInstancia() {
        if (instancia == null) {
            instancia = new GestorCitas();
        }
        return instancia;
    }

    public CompletableFuture<List<Cita>> getCitasVigentes() {
        return citaDAO.listarTodos();
    }

    @Override
    public CompletionStage<Object> cambiarEstado(int id, Cita.Estado estado) {
        // TODO: sin implementar todavía (no relacionado con el bug que estás
        // arreglando ahora mismo; requeriría un endpoint PUT /citas/{id}/estado
        // o reutilizar el guardar(cita) con el estado ya cambiado).
        return null;
    }

    @Override
    public CompletionStage<Object> guardar(Cita nuevaCita) {
        // TODO: sin implementar todavía, por la misma razón que arriba.
        return null;
    }

    /**
     * CORREGIDO: antes este método estaba vacío (no hacía absolutamente
     * nada), así que NuevaCitaPanel podía "guardar" una cita y mostrar el
     * mensaje de éxito sin que ninguna petición HTTP saliera jamás.
     * Ahora construye la Cita real y la envía al servidor a través del DAO.
     */
    public CompletableFuture<Cita> registrarCita(Paciente paciente, String nombreMedico,
                                                 LocalDateTime fechaHora, String motivo, int numeroSala) {
        Cita nuevaCita = new Cita(paciente, nombreMedico, fechaHora, motivo);
        nuevaCita.setSalaId(numeroSala);
        return citaDAO.guardar(nuevaCita);
    }
}