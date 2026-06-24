package Utilidades;

public class ExcepcionesPersonalizadas {

    public static class CitaDuplicadaException extends Exception {
        public CitaDuplicadaException(String mensaje) { super(mensaje); }
    }

    public static class PacienteNoEncontradoException extends Exception {
        public PacienteNoEncontradoException(int id) {
            super("Paciente con ID " + id + " no encontrado.");
        }
    }

    public static class FechaInvalidaException extends Exception {
        public FechaInvalidaException() {
            super("La fecha de la cita no puede ser anterior a hoy.");
        }
    }

    public static class SalaOcupadaException extends Exception {
        public SalaOcupadaException(int numeroSala) {
            super("La sala " + numeroSala + " ya esta ocupada.");
        }
    }
}