package Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL      = "jdbc:postgresql://localhost:5432/ClinicasysDP";
    private static final String USUARIO  = "postgres";
    private static final String PASSWORD = "postgres";

    private static volatile ConexionDB instancia;
    private Connection conexion;

    private ConexionDB() {
        conectar();
    }

    private void conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("[ConexionDB] Conexion establecida con PostgreSQL (" + URL + ")");
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo conectar a la base de datos. Verifica que PostgreSQL este " +
                            "corriendo en Fedora y que la URL/usuario/password sean correctos: " + e.getMessage(), e);
        }
    }

    public static ConexionDB getInstancia() {
        if (instancia == null) {
            synchronized (ConexionDB.class) {
                if (instancia == null) {
                    instancia = new ConexionDB();
                }
            }
        }
        return instancia;
    }

    /** Devuelve la conexion activa; la reabre si se cerro inesperadamente. */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el estado de la conexion: " + e.getMessage(), e);
        }
        return conexion;
    }

    public boolean isConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("[ConexionDB] Conexion cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("[ConexionDB] Error al cerrar la conexion: " + e.getMessage());
        } finally {
            instancia = null;
        }
    }
}
