package obligatorio.obligatorio.Modelo.modelos;

public final class FabricaEstadoPropietario {

    private FabricaEstadoPropietario() { }

    public static EstadoPropietario crearEstado(String nombreEstado, Propietario propietario) {
        if (nombreEstado == null || nombreEstado.equalsIgnoreCase("Habilitado")) {
            return new EstadoPropietarioHabilitado(propietario);
        }
        if (nombreEstado.equalsIgnoreCase("Deshabilitado")) {
            return new EstadoPropietarioDeshabilitado(propietario);
        }
        if (nombreEstado.equalsIgnoreCase("Suspendido")) {
            return new EstadoPropietarioSuspendido(propietario);
        }
        if (nombreEstado.equalsIgnoreCase("Penalizado")) {
            return new EstadoPropietarioPenalizado(propietario);
        }

        return new EstadoPropietarioHabilitado(propietario);
    }
}
