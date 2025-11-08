package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.util.Objects;

public final class Tarifa {
    private final Puesto puesto;
    private final Categoria categoria;
    private BigDecimal monto;

    public Tarifa(Puesto puesto, Categoria categoria, BigDecimal monto) {
        this.puesto = Objects.requireNonNull(puesto);
        this.categoria = Objects.requireNonNull(categoria);
        this.monto = Objects.requireNonNull(monto);
    }

    public Puesto getPuesto() { return puesto; }
    public Categoria getCategoria() { return categoria; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    @Override public boolean equals(Object o){
        if(!(o instanceof Tarifa t)) return false;
        return puesto.equals(t.puesto) && categoria.equals(t.categoria);
    }
    @Override public int hashCode(){return Objects.hash(puesto, categoria);}
    
    // NUEVO MÉTODO: Obtener tarifa por categoría directamente del puesto
    public static Tarifa obtenerTarifaPorCategoria(Puesto puesto, Categoria categoria) {
        for (Tarifa t : puesto.getTarifas()) {
            if (t.getCategoria().equals(categoria)) {
                return t;
            }
        }
        return null; // O lanzar una excepción si se prefiere
    }
}
