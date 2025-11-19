package obligatorio.obligatorio.Modelo.modelos;

public final class FabricaEstadoPropietario {

    private FabricaEstadoPropietario() {
    }

    public static EstadoPropietario crearEstado(String nombreEstado, Propietario propietario) {
        if (nombreEstado == null) {
            throw new IllegalArgumentException("nombreEstado es requerido");
        }
        String normalizado = nombreEstado.trim().toLowerCase();
        switch (normalizado) {
            case "habilitado":
                return new EstadoPropietarioHabilitado(propietario);
            case "deshabilitado":
                return new EstadoPropietarioDeshabilitado(propietario);
            case "suspendido":
                return new EstadoPropietarioSuspendido(propietario);
            case "penalizado":
                return new EstadoPropietarioPenalizado(propietario);
            default:
                throw new IllegalArgumentException("Estado de propietario desconocido: " + nombreEstado);
        }
    }
}

