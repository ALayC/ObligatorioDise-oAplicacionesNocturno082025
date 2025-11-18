package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public abstract class Bonificacion {

    private final String nombre;

    protected Bonificacion(String nombre) {
        this.nombre = Objects.requireNonNull(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public abstract BigDecimal calcularMonto(BigDecimal montoBase,
                                             Propietario propietario,
                                             Vehiculo vehiculo,
                                             Puesto puesto,
                                             LocalDate fecha,
                                             List<Transito> transitosHistoricos);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bonificacion b)) return false;
        return nombre.equalsIgnoreCase(b.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
