package obligatorio.obligatorio.Modelo.modelos;

import java.util.Objects;

public final class Categoria {
    private final String nombre;

    public Categoria(String nombre) { this.nombre = Objects.requireNonNull(nombre); }

    public String getNombre() { return nombre; }

    @Override public boolean equals(Object o){return o instanceof Categoria c && nombre.equalsIgnoreCase(c.nombre);}
    @Override public int hashCode(){return nombre.toLowerCase().hashCode();}
    @Override public String toString(){return nombre;}
}
