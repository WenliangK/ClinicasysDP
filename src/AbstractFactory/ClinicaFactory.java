package AbstractFactory;

import Modelo.Medico;
import Modelo.Sala;

public interface ClinicaFactory {
    Medico crearMedico(Long id, String nombre, String especialidad);
    Sala crearSala(int numero, String descripcion);
}
