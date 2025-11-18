package obligatorio.obligatorio.Modelo.modelos;

public final class EstadoPropietarioDeshabilitado extends EstadoPropietario {

    public EstadoPropietarioDeshabilitado(Propietario propietario) {
        super(propietario);
    }

    @Override
    public String getNombre() {
        return "Deshabilitado";
    }

    @Override
    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        throw new ObligatorioException("El propietario esta deshabilitado. No se pueden asignar bonificaciones");
    }
}
