package obligatorio.obligatorio.Modelo.modelos;

public abstract class EstadoPropietario {
    protected final Propietario propietario;

    protected EstadoPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public abstract String getNombre();

    public abstract void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException;
}

