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
    // 1. Asegúrate de que esto sea estático
    private static GestorCitas instancia;
    private final CitaDAO citaDAO = new CitaDAOImpl();

    // 2. Constructor privado
    private GestorCitas() {}

    // 3. Método para obtener la instancia (el "getInstancia")
    public static synchronized GestorCitas getInstancia() {
        if (instancia == null) {
            instancia = new GestorCitas();
        }
        return instancia;
    }

    // 4. Asegúrate de que este método NUNCA devuelva null
    public CompletableFuture<List<Cita>> getCitasVigentes() {
        // Si citaDAO fuera null, aquí daría error, pero como lo inicializamos arriba, no debería.
        return citaDAO.listarTodos();
    }

    @Override
    public CompletionStage<Object> cambiarEstado(int id, Cita.Estado estado) {
        return null;
    }

    @Override
    public CompletionStage<Object> guardar(Cita nuevaCita) {
        return null;
    }

    public void registrarCita(Paciente paciente, String nombre, LocalDateTime fechaHora, String trim, int numero) {
    }
}