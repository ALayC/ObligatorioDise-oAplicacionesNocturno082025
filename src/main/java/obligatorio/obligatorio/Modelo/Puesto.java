package obligatorio.obligatorio.Modelo;

import java.util.*;

public final class Puesto {
    private final String nombre;
    private final String direccion;
    private final Set<Tarifa> tarifas = new HashSet<>();

    public Puesto(String nombre, String direccion) {
        this.nombre = Objects.requireNonNull(nombre);
        this.direccion = Objects.requireNonNull(direccion);
    }

    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public Set<Tarifa> getTarifas() { return Collections.unmodifiableSet(tarifas); }
    public boolean agregarTarifa(Tarifa t){ return tarifas.add(t); }

    @Override public boolean equals(Object o){return o instanceof Puesto p && nombre.equalsIgnoreCase(p.nombre);}
    @Override public int hashCode(){return nombre.toLowerCase().hashCode();}
}
