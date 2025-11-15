package obligatorio.obligatorio.Modelo.modelos;

public class EstadoPropietarioPenalizado extends EstadoPropietario {
    public EstadoPropietarioPenalizado(Propietario propietario) {
        super(propietario, "Penalizado");
    }

    @Override
    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        throw new ObligatorioException("El propietario está penalizado. No se pueden asignar bonificaciones");
    }
}
