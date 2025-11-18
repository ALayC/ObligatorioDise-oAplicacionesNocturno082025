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
        // Aquí delegamos al experto real: Propietario
        propietario.asignarBonificacionInterna(bonificacion, puesto);
    }
}
