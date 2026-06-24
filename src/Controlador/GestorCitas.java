package Controlador;


import Modelo.Cita;
import Modelo.Paciente;
import Observer.Observador;
import Observer.Sujeto;
import Utilidades.ExcepcionesPersonalizadas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorCitas implements Sujeto {

    private static GestorCitas instancia;
    private List<Cita> citas = new ArrayList<>();
    private List<Observador> observadores = new ArrayList<>();
    private int contadorId = 1;

    private GestorCitas() {}

    // Singleton ligero para el controlador
    public static GestorCitas getInstancia() {
        if (instancia == null) instancia = new GestorCitas();
        return instancia;
    }

    // ─── CRUD de Citas ───────────────────────────────────────────────

    public Cita registrarCita(Paciente paciente, String medico,
                              LocalDateTime fechaHora, String motivo)
            throws ExcepcionesPersonalizadas.FechaInvalidaException {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new ExcepcionesPersonalizadas.FechaInvalidaException();
        }
        Cita nueva = new Cita(contadorId++, paciente, medico, fechaHora, motivo);
        citas.add(nueva);
        notificar("NUEVA_CITA", nueva.getId());
        return nueva;
    }

    public void cambiarEstado(int citaId, Cita.Estado nuevoEstado) {
        citas.stream()
                .filter(c -> c.getId() == citaId)
                .findFirst()
                .ifPresent(c -> {
                    c.setEstado(nuevoEstado);
                    notificar(nuevoEstado.name(), citaId);
                });
    }

    public void cancelarCita(int citaId) {
        cambiarEstado(citaId, Cita.Estado.CANCELADO);
    }

    public List<Cita> getCitas()       { return citas; }
    public List<Cita> getCitasActivas() {
        return citas.stream()
                .filter(c -> c.getEstado() != Cita.Estado.CANCELADO)
                .toList();
    }

    // ─── Observer ────────────────────────────────────────────────────

    @Override
    public void suscribir(Observador o)   { observadores.add(o); }

    @Override
    public void desuscribir(Observador o) { observadores.remove(o); }

    @Override
    public void notificar(String estado, int citaId) {
        for (Observador o : observadores) {
            o.actualizar(estado, citaId);
        }
    }
}
