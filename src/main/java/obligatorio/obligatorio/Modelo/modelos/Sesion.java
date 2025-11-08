package obligatorio.obligatorio.Modelo.modelos;

import java.util.Date;

public class Sesion {
    private final Date fechaIngreso = new Date();
    private final Propietario propietario;

    public Sesion(Propietario propietario) {
        this.propietario = propietario;
    }

    public Date getFechaIngreso() { return fechaIngreso; }
    public Propietario getPropietario() { return propietario; }
}
