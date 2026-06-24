package Singleton;

public class GestorConfiguracion {
    private static volatile GestorConfiguracion instancia;
    private String nombreClinica = "Clinica San Rafael";
    private String version = "1.0";
    private int maximoCitasDiarias = 50;

    private GestorConfiguracion() {}

    public static GestorConfiguracion getInstancia() {
        if (instancia == null) {
            synchronized (GestorConfiguracion.class) {
                if (instancia == null) {
                    instancia = new GestorConfiguracion();
                }
            }
        }
        return instancia;
    }

    public String getNombreClinica()      { return nombreClinica; }
    public String getVersion()            { return version; }
    public int getMaximoCitasDiarias()   { return maximoCitasDiarias; }
    public void setNombreClinica(String n){ this.nombreClinica = n; }
    public void setMaximoCitas(int n)     { this.maximoCitasDiarias = n; }
}
