package obligatorio.obligatorio.Modelo.modelos;

public final class EstadoPropietarioHabilitado extends EstadoPropietario {

    public EstadoPropietarioHabilitado(Propietario propietario) {
        super(propietario);
    }

    @Override
    public String getNombre() {
        return "Habilitado";
    }

    @Override
    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        propietario.asignarBonificacionInterna(bonificacion, puesto);
    }

    @Override
    public void validarPuedeRealizarTransito() throws ObligatorioException {
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
