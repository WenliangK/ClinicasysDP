package AbstractFactory;


import Modelo.Medico;
import Modelo.Sala;

public class PublicaFactory implements ClinicaFactory {
    @Override
    public Medico crearMedico(int id, String nombre, String especialidad) {
        return new Medico(id, nombre, especialidad, "PUBLICO");
    }

    @Override
    public Sala crearSala(int numero, String descripcion) {
        return new Sala(numero, "[SIS] " + descripcion);
    }
}
