package AbstractFactory;

import Modelo.Medico;
import Modelo.Sala;

public interface ClinicaFactory {
    Medico crearMedico(int id, String nombre, String especialidad);
    Sala crearSala(int numero, String descripcion);
}
