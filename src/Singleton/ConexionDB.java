package Singleton;

public class ConexionDB {
    private static volatile ConexionDB instancia;
    private boolean conectado;

    private ConexionDB() {
        this.conectado = true;
        System.out.println("[ConexionDB] Conexion inicializada.");
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

    public boolean isConectado() { return conectado; }

    public void cerrar() {
        this.conectado = false;
        instancia = null;
        System.out.println("[ConexionDB] Conexion cerrada.");
    }
}