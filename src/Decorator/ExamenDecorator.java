package Decorator;

public abstract class ExamenDecorator implements Facturable {
    protected Facturable componente;

    public ExamenDecorator(Facturable componente) {
        this.componente = componente;
    }

    @Override
    public double getCosto() { return componente.getCosto(); }

    @Override
    public String getDescripcion() { return componente.getDescripcion(); }
}
