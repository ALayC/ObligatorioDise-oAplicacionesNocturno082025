package obligatorio.obligatorio.Modelo;

import java.time.LocalDate;
import java.util.Objects;

public final class AsignacionBonificacion {
    private final Propietario propietario;
    private final Puesto puesto;
    private final Bonificacion bonificacion;
    private final LocalDate fechaAsignacion;

    public AsignacionBonificacion(Propietario propietario, Puesto puesto, Bonificacion bonificacion, LocalDate fechaAsignacion) {
        this.propietario = Objects.requireNonNull(propietario);
        this.puesto = Objects.requireNonNull(puesto);
        this.bonificacion = Objects.requireNonNull(bonificacion);
        this.fechaAsignacion = Objects.requireNonNull(fechaAsignacion);
    }

    public Propietario getPropietario() { return propietario; }
    public Puesto getPuesto() { return puesto; }
    public Bonificacion getBonificacion() { return bonificacion; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }

    @Override public boolean equals(Object o){
        if(!(o instanceof AsignacionBonificacion a)) return false;
        return propietario.equals(a.propietario) && puesto.equals(a.puesto);
    }
    @Override public int hashCode(){return Objects.hash(propietario, puesto);}
}
