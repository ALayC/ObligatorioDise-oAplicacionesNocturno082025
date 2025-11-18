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
}