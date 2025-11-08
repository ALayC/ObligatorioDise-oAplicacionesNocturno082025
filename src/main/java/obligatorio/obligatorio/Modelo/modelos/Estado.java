package obligatorio.obligatorio.Modelo.modelos;

import java.util.Objects;

public final class Estado {
    private final String nombre;

    public Estado(String nombre) { this.nombre = Objects.requireNonNull(nombre); }

    public String getNombre() { return nombre; }

    @Override public boolean equals(Object o){return o instanceof Estado e && nombre.equalsIgnoreCase(e.nombre);}
    @Override public int hashCode(){return nombre.toLowerCase().hashCode();}
    @Override public String toString(){return nombre;}
}
