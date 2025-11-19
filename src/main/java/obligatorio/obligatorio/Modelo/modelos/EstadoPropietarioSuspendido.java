package obligatorio.obligatorio.Modelo.modelos;

public final class EstadoPropietarioSuspendido extends EstadoPropietario {

    public EstadoPropietarioSuspendido(Propietario propietario) {
        super(propietario);
    }

    @Override
    public String getNombre() {
        return "Suspendido";
    }

    @Override
    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        propietario.asignarBonificacionInterna(bonificacion, puesto);
    }

    @Override
    public void validarPuedeRealizarTransito() throws ObligatorioException {
        throw new ObligatorioException("El propietario del vehículo está suspendido, no puede realizar tránsitos");
    }

    @Override
    public boolean permiteAplicarBonificaciones() {
        return true;
    }

    @Override
    public boolean registraNotificaciones() {
        return true;
    }

    @Override
    public boolean permiteIngresarAlSistema() {
        return true;
    }
}
