package Observer;

import Modelo.Cita;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Sujeto {
    private final List<Observador> observadores = new CopyOnWriteArrayList<>();

    public void suscribir(Observador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(Observador observador) {
        observadores.remove(observador);
    }

    protected void notificar(String estado, long id) {
        for (Observador observador : observadores) {
            observador.actualizar(estado, id);
        }
    }

    public abstract CompletableFuture<Cita> cambiarEstado(long id, Cita.Estado estado);

    public abstract CompletableFuture<Cita> guardar(Cita nuevaCita);
}
