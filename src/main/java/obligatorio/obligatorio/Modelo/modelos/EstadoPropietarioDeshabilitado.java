package obligatorio.obligatorio.Modelo.modelos;

public class EstadoPropietarioDeshabilitado extends EstadoPropietario {
    public EstadoPropietarioDeshabilitado(Propietario propietario) {
        super(propietario, "Deshabilitado");
    }

    @Override
    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        throw new ObligatorioException("El propietario está deshabilitado. No se pueden asignar bonificaciones");
    }
}
