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
}