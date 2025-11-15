package obligatorio.obligatorio.Modelo.modelos;

import java.time.LocalDateTime;
import java.util.Objects;

public final class AsignacionBonificacion {
    private final Propietario propietario;
    private final Puesto puesto;
    private final Bonificacion bonificacion;
    private final LocalDateTime fechaAsignacion;

    public AsignacionBonificacion(Propietario propietario, Puesto puesto, Bonificacion bonificacion, LocalDateTime fechaAsignacion) {
        this.propietario = Objects.requireNonNull(propietario);
        this.puesto = Objects.requireNonNull(puesto);
        this.bonificacion = Objects.requireNonNull(bonificacion);
        this.fechaAsignacion = Objects.requireNonNull(fechaAsignacion);
    }

    public Propietario getPropietario() { return propietario; }
    public Puesto getPuesto() { return puesto; }
    public Bonificacion getBonificacion() { return bonificacion; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }

    @Override public boolean equals(Object o){
        if(!(o instanceof AsignacionBonificacion a)) return false;
        return propietario.equals(a.propietario) && puesto.equals(a.puesto);
    }
    @Override public int hashCode(){return Objects.hash(propietario, puesto);}
}
