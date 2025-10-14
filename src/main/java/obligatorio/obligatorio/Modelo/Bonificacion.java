package obligatorio.obligatorio.Modelo;

import java.util.Objects;

public final class Bonificacion {
    private final String nombre;

    public Bonificacion(String nombre) { this.nombre = Objects.requireNonNull(nombre); }

    public String getNombre() { return nombre; }

    @Override public boolean equals(Object o){return o instanceof Bonificacion b && nombre.equalsIgnoreCase(b.nombre);}
    @Override public int hashCode(){return nombre.toLowerCase().hashCode();}
    @Override public String toString(){return nombre;}
}
