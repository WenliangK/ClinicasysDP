package Decorator;
public class RadiografiaDecorator extends ExamenDecorator {
    private static final double COSTO_RADIOGRAFIA = 30.0;

    public RadiografiaDecorator(Facturable componente) {
        super(componente);
    }

    @Override
    public double getCosto() {
        return componente.getCosto() + COSTO_RADIOGRAFIA;
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + Radiografia (S/ 30.00)";
    }
}
