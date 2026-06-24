package AbstractFactory;

import Modelo.Medico;
import Modelo.Sala;

public class PrivadaFactory implements ClinicaFactory {
    @Override
    public Medico crearMedico(int id, String nombre, String especialidad) {
        return new Medico(id, nombre, especialidad, "PRIVADO");
    }

    @Override
    public Sala crearSala(int numero, String descripcion) {
        return new Sala(numero, "[Premium] " + descripcion);
    }
}