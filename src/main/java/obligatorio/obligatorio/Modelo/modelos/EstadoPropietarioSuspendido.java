package obligatorio.obligatorio.Modelo.modelos;

public class EstadoPropietarioSuspendido extends EstadoPropietario {
    public EstadoPropietarioSuspendido(Propietario propietario) {
        super(propietario, "Suspendido");
    }

    @Override
    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        throw new ObligatorioException("El propietario está suspendido. No se pueden asignar bonificaciones");
    }
}
