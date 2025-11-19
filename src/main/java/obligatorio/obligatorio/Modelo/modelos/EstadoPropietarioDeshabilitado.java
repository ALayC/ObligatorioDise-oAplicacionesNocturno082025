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

    @Override
    public void validarPuedeRealizarTransito() throws ObligatorioException {
        throw new ObligatorioException("El propietario del vehículo está deshabilitado, no puede realizar tránsitos");
    }

    @Override
    public boolean permiteAplicarBonificaciones() {
        return false;
    }

    @Override
    public boolean registraNotificaciones() {
        return true;
    }

    @Override
    public boolean permiteIngresarAlSistema() {
        return false;
    }

}
