package obligatorio.obligatorio.Modelo;

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
}
