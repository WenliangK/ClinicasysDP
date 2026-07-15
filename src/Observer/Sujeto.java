package Observer;

import Modelo.Cita;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public abstract class Sujeto {
    // Lista de observadores (como tu DashboardCitasPanel)
    private final List<Observador> observadores = new ArrayList<>();

    // Método para agregar un observador
    public void suscribir(Observador obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    // Método para quitar un observador
    public void desuscribir(Observador obs) {
        observadores.remove(obs);
    }

    // EL MÉTODO QUE FALTABA: Notifica a todos los paneles que hubo un cambio
    public void notificar(String estado, int id) {
        for (Observador obs : observadores) {
            obs.actualizar(estado, id);
        }
    }

    public abstract CompletionStage<Object> cambiarEstado(int id, Cita.Estado estado);

    public abstract CompletionStage<Object> guardar(Cita nuevaCita);
}
