package obligatorio.obligatorio.Modelo.modelos;

public final class EstadoPropietarioPenalizado extends EstadoPropietario {

    public EstadoPropietarioPenalizado(Propietario propietario) {
        super(propietario);
    }

    @Override
    public String getNombre() {
        return "Penalizado";
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
        return false;
    }
    //TODO: en penalizado dice que no registra notificaciones, pero en el punto 5 de caso de uso cambiar estado dice que se regiustra sin importar estado actual o anterior
    @Override
    public boolean registraNotificaciones() {
        return false;
    }

    @Override
    public boolean permiteIngresarAlSistema() {
        return true;
    }
}
