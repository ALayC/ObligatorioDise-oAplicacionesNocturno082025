package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.util.Objects;

public final class Bonificacion {
    private final String nombre;

    public Bonificacion(String nombre) { this.nombre = Objects.requireNonNull(nombre); }

    public String getNombre() { return nombre; }

    @Override public boolean equals(Object o){return o instanceof Bonificacion b && nombre.equalsIgnoreCase(b.nombre);}
    @Override public int hashCode(){return nombre.toLowerCase().hashCode();}
    @Override public String toString(){return nombre;}

    public BigDecimal calcularMonto(BigDecimal montoBase) {
        // Implement the logic for calculating the discounted amount.
        // For example, if you have a percentage field:
        // return montoBase.subtract(montoBase.multiply(porcentajeDescuento));
        // Replace with your actual calculation logic.
        return montoBase; // No discount by default
    }
}
