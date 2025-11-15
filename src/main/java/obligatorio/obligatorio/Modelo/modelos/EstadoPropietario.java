package obligatorio.obligatorio.Modelo.modelos;

public abstract class EstadoPropietario {
    protected Propietario propietario;
    protected String nombre;

    public EstadoPropietario(Propietario propietario, String nombre) {
        this.propietario = propietario;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException;
}
