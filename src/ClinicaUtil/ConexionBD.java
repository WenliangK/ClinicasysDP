package ClinicaUtil;

public class ConexionBD {
    // 1. Instancia única (estática y privada)
    private static ConexionBD instancia;
    private String url;

    // 2. Constructor privado para evitar instanciación externa
    private ConexionBD() {
        this.url = "jdbc:mysql://localhost:3306/db_clinica";
        System.out.println("Conexión establecida a: " + this.url);
    }

    // 3. Método global de acceso sincronizado (Thread-Safe)
    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // Método de prueba
    public void ejecutarConsulta(String consulta) {
        System.out.println("Ejecutando en " + url + ": " + consulta);
    }
}