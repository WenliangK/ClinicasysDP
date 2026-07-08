package Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static ConexionDB instancia = null;
    private Connection conexion = null;

    private static final String URL = "jdbc:postgresql://ep-wild-term-aikaptzv-pooler.c-4.us-east-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_zrxZ8YMbn6mw";
    private ConexionDB() {
        conectar();
    }

    public static Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error al conectar a la nube desde obtenerConexion(): " + e.getMessage());
            return null;
        }
    }

    private void conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[ConexionDB] Conexión establecida con PostgreSQL en Neon.");
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo conectar a la base de datos en la nube. Verifica tus credenciales de Neon: " + e.getMessage(), e);
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

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el estado de la conexión: " + e.getMessage(), e);
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
                System.out.println("[ConexionDB] Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("[ConexionDB] Error al cerrar la conexión: " + e.getMessage());
        } finally {
            instancia = null;
        }
    }
}
