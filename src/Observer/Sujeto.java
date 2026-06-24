package Observer;

public interface Sujeto {
    void suscribir(Observador o);
    void desuscribir(Observador o);
    void notificar(String estado, int citaId);
}