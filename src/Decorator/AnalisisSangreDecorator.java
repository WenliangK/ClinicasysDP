package Decorator;

public class AnalisisSangreDecorator extends ExamenDecorator {
    private static final double COSTO_ANALISIS = 20.0;

    public AnalisisSangreDecorator(Facturable componente) {
        super(componente);
    }

    @Override
    public double getCosto() {
        return componente.getCosto() + COSTO_ANALISIS;
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + Analisis de Sangre (S/ 20.00)";
    }
}
